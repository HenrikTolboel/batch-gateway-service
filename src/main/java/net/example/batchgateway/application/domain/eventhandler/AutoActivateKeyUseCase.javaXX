package net.example.batchgateway.application.domain.eventhandler;

import net.example.batchgateway.application.domain.model.events.KeyCreatedEvent;
import net.example.batchgateway.application.domain.model.events.KeyUpdatedEvent;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
// TODO: maybe this class should be called AutoActivateKeyPolicy    introducing the DDD policy concept??
public class AutoActivateKeyUseCase {

    private final KeyRepositoryPort keyRepository;

    public AutoActivateKeyUseCase(final KeyRepositoryPort keyRepository) {
        this.keyRepository = keyRepository;
    }

    @EventListener
    public void onKeyCreatedEvent(final KeyCreatedEvent event) {

        // get Key from Repository, check if Key is marked for AutoActive
        // auto activate key in domain model if needed
        // Save Key + new Domain Events if needed

        keyRepository.findById(event.getKeyId())
                .flatMap(optionalKey -> {
                    if (optionalKey.isPresent()
                            && optionalKey.get().autoActivate()) {
                        return keyRepository.save(optionalKey.get());
                    }
                    return Result.ofOK(optionalKey);
                })
                .mapErr(res -> res);
    }

    @EventListener
    public void onKeyUpdatedEvent(final KeyUpdatedEvent event) {

    }
}
