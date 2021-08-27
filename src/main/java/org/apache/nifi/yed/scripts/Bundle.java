package org.apache.nifi.yed.scripts;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class Bundle {

    private final File workingDirectory;
    private final BundleDescriptor bundleDescriptor;
    private final BundleDescriptor dependencyDescriptor;
    private final ClassLoader classLoader;
    private final URL[] bundleJars;

    public Bundle(Bundle.Builder builder) {
        this.workingDirectory = builder.workingDirectory;
        this.bundleDescriptor = builder.bundleDescriptor;
        this.dependencyDescriptor = builder.dependencyDescriptor;
        this.classLoader = builder.classLoader;
        this.bundleJars = builder.bundleJars;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }

    public BundleDescriptor getBundleDescriptor() {
        return bundleDescriptor;
    }

    public BundleDescriptor getDependencyDescriptor() {
        return dependencyDescriptor;
    }

    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public URL[] getBundleJars() {
        return bundleJars;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bundle bundle = (Bundle) o;
        return workingDirectory.equals(bundle.workingDirectory) &&
                bundleDescriptor.equals(bundle.bundleDescriptor) &&
                Objects.equals(dependencyDescriptor, bundle.dependencyDescriptor);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workingDirectory, bundleDescriptor, dependencyDescriptor);
    }

    @Override
    public String toString() {
        return "Bundle{" + bundleDescriptor + "}";
    }

    public static class Builder {
        public File workingDirectory;
        public BundleDescriptor bundleDescriptor;
        public BundleDescriptor dependencyDescriptor;
        public ClassLoader classLoader;
        public URL[] bundleJars;

        public Bundle build() {
            return new Bundle(this);
        }
    }

    public Builder with() {
        Builder builder = new Builder();
        builder.workingDirectory = this.workingDirectory;
        builder.bundleDescriptor = this.bundleDescriptor;
        builder.dependencyDescriptor = this.dependencyDescriptor;
        builder.classLoader = this.classLoader;
        builder.bundleJars = this.bundleJars;
        return builder;
    }
}
