package net.example.batchgateway.application.domain.model.applicationmodule;

import net.example.batchgateway.application.domain.model.Aggregate;
import net.example.batchgateway.application.domain.model.tenantmodule.TenantId;
import net.example.batchgateway.application.domain.model.events.ApplicationCreatedEvent;

import java.util.Objects;

public final class Application extends Aggregate<ApplicationId> {
    private final ApplicationName applicationName;
    private final TenantId tenantId;
    private final ApplicationType applicationType;

    private Application(final ApplicationId applicationId,
                        final ApplicationName applicationName,
                        final TenantId tenantId,
                        final ApplicationType applicationType) {
        super(applicationId);
        Objects.requireNonNull(applicationName, "applicationName must not be null");
        Objects.requireNonNull(tenantId, "tenantId must not be null");
        Objects.requireNonNull(applicationType, "applicationType must not be null");
        this.applicationName = applicationName;
        this.tenantId = tenantId;
        this.applicationType = applicationType;
    }

    public static Application initExisting(final ApplicationId applicationId,
                                           final ApplicationName applicationName,
                                           final TenantId tenantId,
                                           final ApplicationType applicationType) {
        return new Application(applicationId, applicationName, tenantId, applicationType);
    }

    public static Application create(final ApplicationName applicationName,
                                     final TenantId tenantId,
                                     final ApplicationType applicationType) {
        final Application application = new Application(ApplicationId.generate(), applicationName, tenantId, applicationType);

        application.registerEvent(new ApplicationCreatedEvent(application.getId()));

        return application;
    }

    public TenantId getTenantId() {
        return tenantId;
    }

    public ApplicationName getApplicationName() {
        return applicationName;
    }

    public ApplicationType getApplicationType() {
        return applicationType;
    }
}
