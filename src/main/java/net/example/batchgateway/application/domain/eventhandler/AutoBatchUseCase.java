package net.example.batchgateway.application.domain.eventhandler;

import net.example.batchgateway.application.domain.model.events.BatchCreatedEvent;
import net.example.batchgateway.application.domain.model.events.BatchUpdatedEvent;
import net.example.batchgateway.application.port.output.BatchRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
// TODO: maybe this class should be called AutoActivateKeyPolicy    introducing the DDD policy concept??
public class AutoBatchUseCase {

    private final BatchRepositoryPort batchRepository;

    public AutoBatchUseCase(final BatchRepositoryPort batchRepository) {
        this.batchRepository = batchRepository;
    }

    @EventListener
    public void onBatchCreatedEvent(final BatchCreatedEvent event) {

        // get batch from Repository, check if batch is marked for AutoXXX
        // auto XXX batch in domain model if needed
        // Save batch + new Domain Events if needed

        batchRepository.findById(event.getBatchId())
                .flatMap(optionalBatch -> {
                    if (optionalBatch.isPresent()
                            /*&& optionalBatch.get().autoActivate()*/) {
                        return batchRepository.save(optionalBatch.get());
                    }
                    return Result.ofOK(optionalBatch);
                })
                .mapErr(res -> res);
    }

    @EventListener
    public void onBatchUpdatedEvent(final BatchUpdatedEvent event) {

    }
}
