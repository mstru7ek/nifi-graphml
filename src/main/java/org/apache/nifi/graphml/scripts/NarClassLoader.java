package org.apache.nifi.graphml.scripts;

import java.net.URL;
import java.net.URLClassLoader;

public class NarClassLoader extends URLClassLoader {

    private final URL[] bundleJars;
    private final String bundleCoordinate;

    public NarClassLoader(String bundleCoordinate, URL[] bundleJars, ClassLoader parent) {
        super(bundleJars, parent);
        this.bundleCoordinate = bundleCoordinate;
        this.bundleJars = bundleJars;
    }

    public URL[] getBundleJars() {
        return bundleJars;
    }

    public String getBundleCoordinate() {
        return bundleCoordinate;
    }

    @Override
    public String toString() {
        return "NarClassLoader{" + bundleCoordinate + "}";
    }
}
