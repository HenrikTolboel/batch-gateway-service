package net.example.batchgateway.application.domain.model.events.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import net.example.batchgateway.application.domain.model.applicationmodule.Application;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationId;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationName;
import net.example.batchgateway.application.domain.model.applicationmodule.ApplicationType;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;

import java.util.UUID;

@JsonTypeName("applicationDto")
public class ApplicationDto {
    private String applicationId;
    private String applicationName;
    private String tenantId;
    private ApplicationType applicationType;

    @JsonCreator
    public ApplicationDto(final @JsonProperty("id") String applicationId,
                          final @JsonProperty("applicationName")String applicationName,
                          final @JsonProperty("tenantId") String tenantId,
                          final @JsonProperty("applicationType") ApplicationType applicationType) {
        this.applicationId = applicationId;
        this.applicationName = applicationName;
        this.tenantId = tenantId;
        this.applicationType = applicationType;
    }

    public ApplicationDto(final Application application) {
        this(application.getId().value().toString(),
                application.getApplicationName().value(),
                application.getTenantId().value().toString(),
                application.getApplicationType()
        );
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getTenantId() {
        return tenantId;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }

    public Application toApplication() {
        return Application.initExisting(new ApplicationId(UUID.fromString(applicationId)),
                ApplicationName.create(applicationName),
                new TenantId(UUID.fromString(tenantId)),
                applicationType);
    }
}
