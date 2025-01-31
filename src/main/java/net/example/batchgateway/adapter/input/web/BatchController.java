package net.example.batchgateway.adapter.input.web;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.example.batchgateway.adapter.input.web.dto.GenerateDTO;
import net.example.batchgateway.adapter.input.web.dto.GenerateWithIdDTO;
import net.example.batchgateway.adapter.input.web.dto.ListRevisionsDTO;
import net.example.batchgateway.application.domain.model.keymodule.*;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.usermodule.UserId;
import net.example.batchgateway.application.port.DomainError;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.input.*;
import net.example.batchgateway.config.JwtAuthorization;
import net.example.utils.dichotomy.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Optional;
import java.util.UUID;

@Tag(name = "Batch", description = "Batch management APIs")
@RestController
@RequestMapping("/batches")
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

    private static Key okmapper(final Optional<Key> optionalKey) {
        if (optionalKey.isEmpty()) {
            throw new ProblemDetailException(HttpStatus.NOT_FOUND, "Key not found", URI.create("info:key-not-found"));
        }
        return optionalKey.get();
    }


    @Operation(
//            tags = {"keys", "otherkeys"},
            summary = "Get list of revisions for specified Key",
            description = "a description given",
            parameters = {
                    @Parameter(name = "applicationType",
                            required = true)
            },
            responses = {
                    @ApiResponse(responseCode = "200", description = "Revisions for specified Key returned",
                            content = {
                                    @Content(mediaType = "application/json",
                                            schema = @Schema(implementation = ListRevisionsDTO.class),
                                            examples = @ExampleObject("{\"list\": \"value\"}")
//                                            array = @ArraySchema(schema = @Schema(implementation = ListRevisionsDTO.RevisionDTO.class))
                                    )
                            }
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }
                    ),
                    @ApiResponse(responseCode = "404", description = "Specified Key not found",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }
                    )

            }
    )
    @GetMapping("/{keyId}/listRevisions")
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ListRevisionsDTO listRevisions(final @PathVariable String keyId) {

        final Result<Key, Object> result = queryKeyUseCase.findKeyById(new FindKeyQuery(
                        TenantId.generate(),
                        UserId.generate(),
                        new KeyId(UUID.fromString(keyId))))
                .biMap(BatchController::okmapper, BatchController::errormapper);

        return ListRevisionsDTO.create(result.expect());
    }


    @Operation(
            summary = "Generate Key",
            description = "a description given",
//            parameters = {
//                    @Parameter(name = "applicationType",
//                            required = true)
//            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = GenerateDTO.class
                            )
                    ),
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Key Created",
                            headers = {
                                    @Header(name = "Location", description = "Id of created Key")
                            },
                            content = {@Content}
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }

                    ),
                    @ApiResponse(responseCode = "404", description = "Specified Key not found",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }
                    )

            }
    )
//    @PostMapping(params = "xmlType=GenerateDTO")
    @PostMapping
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ResponseEntity<Object> generate(@RequestBody final GenerateDTO generateDTO) {

        final GenerateKeyCommand command = new GenerateKeyCommand(
                TenantId.generate(),
                UserId.generate(),
                KeyName.create(generateDTO.keyName()),
                KeyType.create(generateDTO.keyType().keyType()),
                generateDTO.keyMaterialDetails().toKeyMaterialDetails(),
                generateDTO.expire(),
                generateDTO.autoActivate(),
                generateDTO.autoRotate(),
                generateDTO.keyLifeCycleDays(),
                CustomAttributes.fromMap(generateDTO.customAttributes())
        );

        final Result<Key, Object> result = createKeyUseCase.create(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.created(URI.create(result.expect().getId().value().toString())).build();
    }

    @Operation(
            summary = "Generate Key",
            description = "a description given",
//            parameters = {
//                    @Parameter(name = "applicationType",
//                            required = true)
//            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = GenerateWithIdDTO.class
                            )
                    ),
                    required = true
            ),
            responses = {
                    @ApiResponse(responseCode = "201", description = "Key Created",
                            headers = {
                                    @Header(name = "Location", description = "Id of created Key")
                            },
                            content = {@Content}
                    ),
                    @ApiResponse(responseCode = "400", description = "Bad Request",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }

                    ),
                    @ApiResponse(responseCode = "404", description = "Specified Key not found",
                            content = {
                                    @Content(mediaType = MediaType.APPLICATION_PROBLEM_JSON_VALUE,
                                            schema = @Schema(implementation = ProblemDetail.class))
                            }
                    )

            }
    )
//    @PostMapping(params = "xmlType=GenerateWithIdDTO")
    @PostMapping("/withid")
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ResponseEntity<Object> generate2(@RequestBody final GenerateWithIdDTO generateDTO) {

        final GenerateKeyWithIdCommand command = new GenerateKeyWithIdCommand(
                UUID.fromString(generateDTO.id()),
                TenantId.generate(),
                UserId.generate(),
                KeyName.create(generateDTO.keyName()),
                KeyType.create(generateDTO.keyType().keyType()),
                generateDTO.keyMaterialDetails().toKeyMaterialDetails(),
                generateDTO.expire(),
                generateDTO.autoActivate(),
                generateDTO.autoRotate(),
                generateDTO.keyLifeCycleDays(),
                CustomAttributes.fromMap(generateDTO.customAttributes())
        );

        final Result<Key, Object> result = createKeyUseCase.create(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.created(URI.create(result.expect().getId().value().toString())).build();
    }

    @PutMapping("/{keyId}/activate")
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ResponseEntity<Object> activate(Authentication authentication, @PathVariable final String keyId) {
        final ActivateKeyCommand command = new ActivateKeyCommand(
                TenantId.generate(),
                UserId.generate(),
                new KeyId(UUID.fromString(keyId))
        );

        keyManagementUseCase.activateKey(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{keyId}/deactivate")
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ResponseEntity<Object> deactivate(@PathVariable final String keyId) {
        final DeactivateKeyCommand command = new DeactivateKeyCommand(
                TenantId.generate(),
                UserId.generate(),
                new KeyId(UUID.fromString(keyId))
        );

        keyManagementUseCase.deactivateKey(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{keyId}/deactivateprevious")
    @JwtAuthorization(acceptNoAuthorizationHeader = true)
    public ResponseEntity<Object> deactivateprevious(@PathVariable final String keyId) {
        final DeactivatePreviousKeyCommand command = new DeactivatePreviousKeyCommand(
                TenantId.generate(),
                UserId.generate(),
                new KeyId(UUID.fromString(keyId))
        );

        keyManagementUseCase.deactivatePreviousKey(command)
                .mapErr(BatchController::errormapper);

        return ResponseEntity.noContent().build();
    }

}
