package net.example.batchgateway.archunit;

import java.util.List;

import com.tngtech.archunit.core.domain.JavaClasses;
import static com.tngtech.archunit.base.DescribedPredicate.*;
import static com.tngtech.archunit.lang.conditions.ArchConditions.*;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.*;

class ArchitectureElement {

    public final String basePackage;

    public ArchitectureElement(final String basePackage) {
        this.basePackage = basePackage;
    }

    public String fullQualifiedPackage(final String relativePackage) {
        return this.basePackage + "." + relativePackage;
    }

    public static void denyDependency(final String fromPackageName, final String toPackageName, final JavaClasses classes) {
        noClasses()
                .that()
                .resideInAPackage(matchAllClassesInPackage(fromPackageName))
                .should()
                .dependOnClassesThat()
                .resideInAnyPackage(matchAllClassesInPackage(toPackageName))
                .check(classes);
    }

    public static void denyAnyDependency(
            final List<String> fromPackages, final List<String> toPackages, final JavaClasses classes) {
        for (final String fromPackage : fromPackages) {
            for (final String toPackage : toPackages) {
                denyDependency(fromPackage, toPackage, classes);
            }
        }
    }

    public static String matchAllClassesInPackage(final String packageName) {
        return packageName + "..";
    }

    public void denyEmptyPackage(final String packageName, final JavaClasses classes) {
        classes()
                .that()
                .resideInAPackage(matchAllClassesInPackage(packageName))
                .should(containNumberOfElements(greaterThanOrEqualTo(1)))
                .check(classes);
    }

    public void denyEmptyPackages(final List<String> packages, final JavaClasses classes) {
        for (final String packageName : packages) {
            denyEmptyPackage(packageName, classes);
        }
    }
}
