package net.example.batchgateway.application.domain.model.keymodule;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.BiConsumer;

public class KeyRevisions {


    @JsonProperty("keyRevisions")
    private final Map<String, KeyRevision> keyRevisions;

    public KeyRevisions() {
        this.keyRevisions = new HashMap<>();
    }

    public void add(final KeyRevision keyRevision) {
        keyRevisions.put(keyRevision.getId().asString(), keyRevision);
    }

    public KeyRevision get(final KeyRevisionId keyRevisionId) {
        return keyRevisions.get(keyRevisionId.asString());
    }

    public boolean containsKey(final KeyRevisionId keyRevisionId) {
        return keyRevisions.containsKey(keyRevisionId.asString());
    }

    class CreatedComparator implements Comparator<KeyRevision> {
        @Override
        public int compare(KeyRevision o1, KeyRevision o2) {
            return o1.getCreated().compareTo(o2.getCreated());
        }
    }

    protected List<KeyRevision> getKeyRevisionsAsSortedList() {
        var list = new ArrayList<>(keyRevisions.values());

        list.sort(new CreatedComparator());

        return list;
    }



    @Override
    public final boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof KeyRevisions that)) {
            return false;
        }

        return keyRevisions.equals(that.keyRevisions);
    }

    @Override
    public int hashCode() {
        return keyRevisions.hashCode();
    }

    public void forEach(BiConsumer<? super KeyRevisionId, ? super KeyRevision> action) {
        keyRevisions.forEach((str, keyRevision)  -> {
            action.accept(new KeyRevisionId(UUID.fromString(str)), keyRevision);
        });
    }

}
