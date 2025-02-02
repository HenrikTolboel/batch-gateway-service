package net.example.batchgateway.adapter.input.web;

import io.swagger.v3.oas.annotations.tags.Tag;
import net.example.batchgateway.adapter.input.web.dto.CreateBatchDTO;
import net.example.batchgateway.adapter.input.web.dto.ViewBatchDTO;
import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.domain.model.BatchName;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.DomainError;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.input.*;
import net.example.utils.dichotomy.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Batch", description = "Batch management APIs")
@RestController
@RequestMapping("/batch")
public class BatchController {

    private final QueryBatchUseCasePort queryBatchUseCase;
    private final CreateBatchUseCasePort createBatchUseCase;

    public BatchController(final QueryBatchUseCasePort queryBatchUseCase,
                           final CreateBatchUseCasePort createBatchUseCase) {
        this.queryBatchUseCase = queryBatchUseCase;
        this.createBatchUseCase = createBatchUseCase;
    }

    private static Object errormapper(final GeneralError error) {
        switch (error) {
            case DomainError.KeyNotFoundError e ->
                    throw new ProblemDetailException(HttpStatus.NOT_FOUND, "Key not found", URI.create("info:key-not-found"));
            case DomainError.KeyAlreadyExistError e ->
                    throw new ProblemDetailException(HttpStatus.CONFLICT, "Key already exist:" + e.keyId().value().toString(), URI.create("info:key-already-exist"));
            case ServiceError.DatabaseErrorMessage e ->
                    throw new ProblemDetailException(HttpStatus.SERVICE_UNAVAILABLE, e.message(), URI.create("error:database-error"));
            default -> throw new IllegalStateException("Unexpected value: " + error);
        }
    }

    private static Batch okmapper(final Optional<Batch> optionalBatch) {
        if (optionalBatch.isEmpty()) {
            throw new ProblemDetailException(HttpStatus.NOT_FOUND, "Batch not found", URI.create("info:batch-not-found"));
        }
        return optionalBatch.get();
    }


//    @Operation(
////            tags = {"keys", "otherkeys"},
//            summary = "Get list of revisions for specified Key",
//            description = "a description given",
//            parameters = {
//                    @Parameter(name = "applicationType",
//                            required = true)
//            },
//            responses = {
//                    @ApiResponse(responseCode = "200", description = "Revisions for specified Key returned",
//                            content = {
//                                    @Content(mediaType = "application/json",
//                                            schema = @Schema(implementation = ListRevisionsDTO.class),
//                                            examples = @ExampleObject("{\"list\": \"value\"}")
////                                            array = @ArraySchema(schema = @Schema(implementation = ListRevisionsDTO.RevisionDTO.class))
//                                    )
//                            }
//                    ),
//                    @ApiResponse(responseCode = "400", description = "Bad Request",
//                            content = {
//                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
//                                            schema = @Schema(implementation = ProblemDetail.class))
//                            }
//                    ),
//                    @ApiResponse(responseCode = "404", description = "Specified Key not found",
//                            content = {
//                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
//                                            schema = @Schema(implementation = ProblemDetail.class))
//                            }
//                    )
//
//            }
//    )
    @GetMapping("/{keyId}/listRevisions")
    public ViewBatchDTO listRevisions(final @PathVariable String batchId) {

        final Result<Batch, Object> result = queryBatchUseCase.findBatchById(new FindBatchQuery(
                        TenantId.generate(),
                        UserId.generate(),
                        new BatchId(UUID.fromString(batchId))))
                .biMap(BatchController::okmapper, BatchController::errormapper);

        return ViewBatchDTO.create(result.expect());
    }


//    @Operation(
//            summary = "Generate Key",
//            description = "a description given",
////            parameters = {
////                    @Parameter(name = "applicationType",
////                            required = true)
////            },
//            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
//                    content = @Content(
//                            mediaType = "application/json",
//                            schema = @Schema(
//                                    implementation = GenerateDTO.class
//                            )
//                    ),
//                    required = true
//            ),
//            responses = {
//                    @ApiResponse(responseCode = "201", description = "Key Created",
//                            headers = {
//                                    @Header(name = "Location", description = "Id of created Key")
//                            },
//                            content = {@Content}
//                    ),
//                    @ApiResponse(responseCode = "400", description = "Bad Request",
//                            content = {
//                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
//                                            schema = @Schema(implementation = ProblemDetail.class))
//                            }
//
//                    ),
//                    @ApiResponse(responseCode = "404", description = "Specified Key not found",
//                            content = {
//                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
//                                            schema = @Schema(implementation = ProblemDetail.class))
//                            }
//                    )
//
//            }
//    )
    @PostMapping
    public ResponseEntity<Object> create(@RequestBody final CreateBatchDTO createBatchDTO) {

        final CreateBatchCommand command = new CreateBatchCommand(
                BatchId.generate(),
                BatchName.create(createBatchDTO.batchName()),
                TenantId.generate()
        );

        final Result<Batch, Object> result = createBatchUseCase.create(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.created(URI.create(result.expect().getId().value().toString())).build();
    }



}
