package net.example.batchgateway.archunit;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaPackage;

public class Adapters extends ArchitectureElement {

    private final HexagonalArchitecture parentContext;
    private final List<String> incomingAdapterPackages = new ArrayList<>();
    private final List<String> outgoingAdapterPackages = new ArrayList<>();

    public Adapters(final HexagonalArchitecture parentContext, final String basePackage) {
        super(basePackage);
        this.parentContext = parentContext;
    }

    public Adapters incoming(final String packageName) {
        this.incomingAdapterPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public Adapters outgoing(final String packageName) {
        this.outgoingAdapterPackages.add(fullQualifiedPackage(packageName));
        return this;
    }

    public void expandAdapterPackages(final JavaClasses classes) {
        // foreach adapter package ending with ".*" expand it using the existing classes
        // ".*" is one level (* is not a dot)

        doExpandAdapterPackage(outgoingAdapterPackages, classes);
        doExpandAdapterPackage(incomingAdapterPackages, classes);
    }

    private void doExpandAdapterPackage(final List<String> packageList, final JavaClasses classes) {
        // foreach adapter package ending with ".*" expand it using the existing classes
        // ".*" is one level (* is not a dot)

        final List<String> tmpList = new ArrayList<>();
        final Iterator it = packageList.iterator();

        while(it.hasNext()) {
            final var packageName = (String) it.next();
            if (packageName.endsWith(".*")) {
                final var set = classes.getPackage(packageName.substring(0, packageName.length() - 2)).getSubpackages();

                final Iterator setIt = set.iterator();
                while(setIt.hasNext()) {
                    final var o = (JavaPackage) setIt.next();
                    tmpList.add(o.getName());
                }
            } else {
                tmpList.add(packageName);
            }
        }
        packageList.clear();
        packageList.addAll(tmpList);

    }

    public List<String> allAdapterPackages() {
        final List<String> allAdapters = new ArrayList<>();
        allAdapters.addAll(incomingAdapterPackages);
        allAdapters.addAll(outgoingAdapterPackages);
        return allAdapters;
    }

    public HexagonalArchitecture and() {
        return parentContext;
    }

    public String getBasePackage() {
        return basePackage;
    }

    public void dontDependOnEachOther(final JavaClasses classes) {
        final List<String> allAdapters = allAdapterPackages();
        for (final String adapter1 : allAdapters) {
            for (final String adapter2 : allAdapters) {
                if (!adapter1.equals(adapter2)) {
                    denyDependency(adapter1, adapter2, classes);
                }
            }
        }
    }

    public void doesNotDependOn(final String packageName, final JavaClasses classes) {
        denyDependency(this.basePackage, packageName, classes);
    }

    public void doesNotContainEmptyPackages(final JavaClasses classes) {
        denyEmptyPackages(allAdapterPackages(), classes);
    }
}
