package net.example.batchgateway.adapter.input.web.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.example.batchgateway.application.domain.model.keymodule.Key;
import net.example.batchgateway.application.domain.model.keymodule.KeyRevisionId;
import net.example.batchgateway.application.domain.model.keymodule.KeyState;
import net.example.batchgateway.application.domain.model.keymodule.KeyValueId;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

public class ListRevisionsDTO {

    private final List<RevisionDTO> revisions;
    private final Instant keyCreated;
    private final String keyName;

    public record RevisionDTO(KeyRevisionId keyRevisionId, KeyValueId keyValueId, KeyState keyState) {}

    public List<RevisionDTO> getRevisions() {
        return revisions;
    }

    public Instant getKeyCreated() {
        return keyCreated;
    }

    public String getKeyName() {
        return keyName;
    }

    @JsonCreator
    public ListRevisionsDTO(@JsonProperty("keyCreated") final Instant keyCreated,
                            @JsonProperty("keyName") final String keyName,
                            @JsonProperty("revisions") final List<RevisionDTO> revisions) {
        this.keyCreated = keyCreated;
        this.keyName = keyName;
        this.revisions = revisions;
    }

    public static ListRevisionsDTO create(final Key key) {
        final List<RevisionDTO> revisions = new ArrayList<>();
        key.forEachRevision((keyRevision, keyValue) -> {
            revisions.add(new RevisionDTO(keyRevision.getId(), keyValue.getId(), keyValue.getKeyState()));
        });
        return new ListRevisionsDTO(key.getCreated(), key.getName().value(), revisions);
    }
}
