package net.example.batchgateway.adapter.output.keyrepository;

import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyId;
import net.example.batchgateway.application.port.GeneralError;
import net.example.batchgateway.application.port.output.KeyRepositoryPort;
import net.example.utils.dichotomy.Result;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Component
@ConditionalOnProperty(name = "storage.service", havingValue = "memory", matchIfMissing = true)
class MemoryKeyRepository implements KeyRepositoryPort {

    private final Map<KeyId, Key> keys = new HashMap<>();

    @Override
    public Result<Optional<Key>, GeneralError> findById(final KeyId keyId) {
        final Optional<Key> result;
        if (keys.containsKey(keyId)) {
            result = Optional.of(keys.get(keyId));
        } else {
            result = Optional.empty();
        }
        return Result.ofOK(result);
    }

    @Override
    @Transactional
    public Result<Key, GeneralError> save(final Key key) {
        keys.put(key.getId(), key);
        return Result.ofOK(key);
    }
}
