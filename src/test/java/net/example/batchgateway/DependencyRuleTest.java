package net.example.batchgateway;

import com.tngtech.archunit.core.importer.ClassFileImporter;

import com.tngtech.archunit.core.importer.ImportOption;
import net.example.batchgateway.archunit.HexagonalArchitecture;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class DependencyRuleTest {

    @Test
    void validateHexagonalArchitecture() {
        HexagonalArchitecture.basePackage(this.getClass().getPackageName())
                .withApplicationLayer("application")
                .domainModel("domain.model")
                .domainService("domain.service")
                .domainEventHandler("domain.eventhandler")
                .services("service")
                .incomingPorts("port.input")
                .outgoingPorts("port.output")
                .and()
                .withAdaptersLayer("adapter")
                .incoming("input.*")
                .outgoing("output.*")
                .and()
                .withConfiguration("config")
                .check(new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(this.getClass().getPackageName() + ".."));
    }

    @Test
    void domainModelDoesNotDependOnOutside() {
        noClasses()
                .that()
                .resideInAPackage(this.getClass().getPackageName() + ".application.domain.model..")
                .should()
                .dependOnClassesThat()
                .resideOutsideOfPackages(
                        this.getClass().getPackageName() + ".application.domain.model..",
                        "lombok..",
                        "java..",
                        "com.fasterxml.jackson.annotation.."
                )
                .check(new ClassFileImporter()
                        .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
                        .importPackages(this.getClass().getPackageName() + ".."));
    }

}
