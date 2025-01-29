package net.example.batchgateway.archunit;


import java.util.ArrayList;
import java.util.List;

import com.tngtech.archunit.core.domain.JavaClasses;

public class ApplicationLayer extends ArchitectureElement {

    private final HexagonalArchitecture parentContext;
    private final List<String> incomingPortsPackages = new ArrayList<>();
    private final List<String> outgoingPortsPackages = new ArrayList<>();
    private final List<String> servicePackages = new ArrayList<>();
    private final List<String> domainModelPackages = new ArrayList<>();
    private final List<String> domainServicePackages = new ArrayList<>();
    private final List<String> domainEventHandlerPackages = new ArrayList<>();

    public ApplicationLayer(final String basePackage, final HexagonalArchitecture parentContext) {
        super(basePackage);
        this.parentContext = parentContext;
    }

    public ApplicationLayer incomingPorts(final String packageName) {
        this.incomingPortsPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer outgoingPorts(final String packageName) {
        this.outgoingPortsPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer services(final String packageName) {
        this.servicePackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer domainModel(final String packageName) {
        this.domainModelPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer domainService(final String packageName) {
        this.domainServicePackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public ApplicationLayer domainEventHandler(final String packageName) {
        this.domainEventHandlerPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public HexagonalArchitecture and() {
        return parentContext;
    }

    public void doesNotDependOn(final String packageName, final JavaClasses classes) {
        denyDependency(this.basePackage, packageName, classes);
    }

    public void incomingAndOutgoingPortsDoNotDependOnEachOther(final JavaClasses classes) {
        denyAnyDependency(this.incomingPortsPackages, this.outgoingPortsPackages, classes);
        denyAnyDependency(this.outgoingPortsPackages, this.incomingPortsPackages, classes);
    }

    private List<String> allPackages() {
        final List<String> allPackages = new ArrayList<>();
        allPackages.addAll(incomingPortsPackages);
        allPackages.addAll(outgoingPortsPackages);
        allPackages.addAll(servicePackages);
        allPackages.addAll(domainModelPackages);
        allPackages.addAll(domainServicePackages);
        allPackages.addAll(domainEventHandlerPackages);
        return allPackages;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void doesNotContainEmptyPackages(final JavaClasses classes) {
        denyEmptyPackages(allPackages(), classes);
    }
}
