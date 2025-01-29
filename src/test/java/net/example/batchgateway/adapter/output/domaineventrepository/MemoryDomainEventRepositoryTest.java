package net.example.batchgateway.adapter.output.domaineventrepository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import net.example.batchgateway.application.domain.model.applicationmodule.Application;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationName;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationType;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationTypeEnum;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.events.ApplicationCreatedEvent;
import net.example.batchgateway.application.domain.model.events.DomainEvent;
import net.example.batchgateway.application.domain.model.events.ItemIdAddedToUser;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.DomainEventRepositoryPort;
import net.example.batchgateway.config.SpringBootObjectMapper;
import net.example.utils.dichotomy.Empty;
import net.example.utils.dichotomy.Result;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MemoryDomainEventRepositoryTest {

    private final ObjectMapper objectMapper = SpringBootObjectMapper.getObjectMapper();
    private final DomainEventRepositoryPort repository = new MemoryDomainEventRepository();





    @Test
    void save() {

        final ApplicationCreatedEvent event = new ApplicationCreatedEvent(ApplicationId.generate());

        final Result<Empty, GeneralError> saveRes = repository.save(event);

        final Result<List<DomainEvent>, GeneralError> findRes = repository.findTop10DomainEvents();

        assertTrue(findRes.isOK());

        final List<DomainEvent> list = findRes.expect();

        assertFalse(list.isEmpty());
        assertEquals(event, list.getFirst());
    }

    @Test
    void testEvent() {

        final Application application = Application.create(ApplicationName.create("appName"),
                TenantId.generate(),
                ApplicationType.create(ApplicationTypeEnum.CQL));

        final var event = new ItemIdAddedToUser("ItemId",
                1234L,
                application);

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

        Application back  = ((ItemIdAddedToUser)result).getApplication();

        assertInstanceOf(ItemIdAddedToUser.class, result);

        assertEquals(application, back);
    }
}
