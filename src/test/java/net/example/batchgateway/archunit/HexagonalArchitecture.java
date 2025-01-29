package net.example.batchgateway.archunit;

import com.tngtech.archunit.core.domain.JavaClasses;

public class HexagonalArchitecture extends ArchitectureElement {

    private Adapters adapters;
    private ApplicationLayer applicationLayer;
    private Configuration configuration;

    public static HexagonalArchitecture basePackage(final String basePackage) {
        return new HexagonalArchitecture(basePackage);
    }

    public HexagonalArchitecture(final String basePackage) {
        super(basePackage);
    }

    public Adapters withAdaptersLayer(final String adaptersPackage) {
        this.adapters = new Adapters(this, fullQualifiedPackage(adaptersPackage));
        return this.adapters;
    }

    public ApplicationLayer withApplicationLayer(final String applicationPackage) {
        this.applicationLayer = new ApplicationLayer(fullQualifiedPackage(applicationPackage), this);
        return this.applicationLayer;
    }

    public HexagonalArchitecture withConfiguration(final String packageName) {
        this.configuration = new Configuration(this, fullQualifiedPackage(packageName));
        return this;
    }

    public void check(final JavaClasses classes) {
        this.adapters.expandAdapterPackages(classes);
        this.adapters.doesNotContainEmptyPackages(classes);
        this.adapters.dontDependOnEachOther(classes);
        this.applicationLayer.doesNotContainEmptyPackages(classes);
        this.applicationLayer.doesNotDependOn(this.adapters.getBasePackage(), classes);
        this.applicationLayer.incomingAndOutgoingPortsDoNotDependOnEachOther(classes);
        this.configuration.doesNotDependOn(this.adapters.getBasePackage(), classes);
        this.configuration.doesNotDependOn(this.applicationLayer.getBasePackage(), classes);
    }
}
