package net.example.batchgateway.adapter.output.domaineventrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.example.batchgateway.application.domain.model.Batch;
import net.example.batchgateway.application.domain.model.BatchId;
import net.example.batchgateway.application.domain.model.BatchName;
import net.example.batchgateway.application.domain.model.events.BatchCreatedEvent;
import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MemoryDomainEventRepositoryTest {

    private final ObjectMapper objectMapper = SpringBootObjectMapper.getObjectMapper();
    private final DomainEventRepositoryPort repository = new MemoryDomainEventRepository();





    @Test
    void save() {

        final BatchCreatedEvent event = new BatchCreatedEvent(BatchId.generate());

        final Result<Empty, GeneralError> saveRes = repository.save(event);

        final Result<List<DomainEvent>, GeneralError> findRes = repository.findTop10DomainEvents();

        assertTrue(findRes.isOK());

        final List<DomainEvent> list = findRes.expect();

        assertFalse(list.isEmpty());
        assertEquals(event, list.getFirst());
    }

    @Test
    void testEvent() {

        final Batch application = Batch.create(BatchName.create("appName"),
                TenantId.generate());

        final var event = new BatchCreatedEvent(BatchId.generate());

        final String json;
        try {
            json = objectMapper.writeValueAsString(event);

        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        final DomainEvent result;
        try {
            result = objectMapper.readValue(json, DomainEvent.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        BatchId back  = ((BatchCreatedEvent)result).getBatchId();

        assertInstanceOf(BatchCreatedEvent.class, result);

        assertEquals(event.getBatchId(), back);
    }
}
