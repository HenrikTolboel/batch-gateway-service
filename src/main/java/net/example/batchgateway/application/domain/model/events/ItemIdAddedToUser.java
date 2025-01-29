package net.example.batchgateway.application.domain.model.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.applicationmodule.Application;
import net.example.batchgateway.application.domain.model.events.dto.ApplicationDto;

import java.time.Instant;

public class ItemIdAddedToUser extends DomainEvent {
    private final String itemId;
    private final Long quantity;
    private final ApplicationDto applicationDto;

    @JsonCreator
    ItemIdAddedToUser(final @JsonProperty("id") DomainEventId id,
                      final @JsonProperty("timestamp") Instant timestamp,
                      final @JsonProperty("itemId") String itemId,
                      final @JsonProperty("quantity") Long quantity,
                      final @JsonProperty("applicationDto") ApplicationDto applicationDto) {
        super(id, timestamp);
        this.itemId = itemId;
        this.quantity = quantity;
        this.applicationDto = applicationDto;
    }

    public ItemIdAddedToUser(final String itemId, final Long quantity, final Application application) {
        super();
        this.itemId = itemId;
        this.quantity = quantity;
        this.applicationDto = new ApplicationDto(application);
    }

    public String getItemId() {
        return itemId;
    }

    public ApplicationDto getApplicationDto() {
        return applicationDto;
    }

    public Application getApplication() {
        return applicationDto.toApplication();
    }

    public Long getQuantity() {
        return quantity;
    }

}
