package net.example.batchgateway.adapter.input.web;

import net.example.batchgateway.adapter.input.web.dto.GenerateDTO;
import net.example.batchgateway.adapter.input.web.dto.GenerateWithIdDTO;
import net.example.batchgateway.adapter.input.web.dto.KcvTypeDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyMaterial3DESDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyMaterialAESDTO;
import net.example.batchgateway.adapter.input.web.dto.KeyTypeDTO;
import net.example.batchgateway.adapter.input.web.dto.ListRevisionsDTO;
import net.example.batchgateway.application.domain.model.keymodule.*;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.ServiceError;
import net.example.batchgateway.application.port.input.CreateBatchUseCasePort;
import net.example.batchgateway.application.port.input.CreateKeyUseCasePort;
import net.example.batchgateway.application.port.input.GenerateKeyCommand;
import net.example.batchgateway.application.port.input.GenerateKeyWithIdCommand;
import net.example.batchgateway.application.port.input.KeyManagementUseCasePort;
import net.example.batchgateway.application.port.input.QueryBatchUseCasePort;
import net.example.batchgateway.application.port.input.QueryKeyUseCasePort;
import net.example.batchgateway.config.CreateJwtToken;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
class BatchControllerTest {

    @Autowired
    private WebTestClient webTestClient;

//    @MockitoBean
//    private QueryBatchUseCasePort queryKeyUseCase;
//
//    @MockitoBean
//    private CreateBatchUseCasePort createKeyUseCase;

    @LocalServerPort
    private int serverPort;


    @Test
    void contextLoads() {
    }


}
