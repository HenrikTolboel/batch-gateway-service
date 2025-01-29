package net.example.batchgateway.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;

public class Configuration extends ArchitectureElement {

    private final HexagonalArchitecture parentContext;

    public Configuration(final HexagonalArchitecture parentContext, final String basePackage) {
        super(basePackage);
        this.parentContext = parentContext;
    }

    public HexagonalArchitecture and() {
        return parentContext;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void doesNotDependOn(final String packageName, final JavaClasses classes) {
        denyDependency(this.basePackage, packageName, classes);
    }
}
