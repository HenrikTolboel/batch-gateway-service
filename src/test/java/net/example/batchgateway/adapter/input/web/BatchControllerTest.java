package net.example.batchgateway.adapter.input.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.mockito.ArgumentMatchers.any;

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
