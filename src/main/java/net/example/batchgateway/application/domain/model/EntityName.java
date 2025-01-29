package net.example.batchgateway.application.domain.model;

public class EntityName extends Name {

    public EntityName(final String name) {
        super(name);
        if (name.contains(" ")) {
            throw new IllegalArgumentException("Name contains spaces");
        }
    }
}
