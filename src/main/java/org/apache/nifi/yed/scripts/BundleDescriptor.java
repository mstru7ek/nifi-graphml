package org.apache.nifi.yed.scripts;

import java.util.Objects;

public class BundleDescriptor {

    private final String group;
    private final String id;
    private final String version;
    private final String coordinate;

    public BundleDescriptor(String group, String id, String version) {
        this.group = group;
        this.id = id;
        this.version = version;
        this.coordinate = group + ":" + id + ":" + version;
    }

    public String getGroup() {
        return group;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getCoordinate() {
        return coordinate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BundleDescriptor that = (BundleDescriptor) o;
        return group.equals(that.group) && id.equals(that.id) && version.equals(that.version);
    }

    @Override
    public int hashCode() {
        return Objects.hash(group, id, version);
    }

    @Override
    public String toString() {
        return "{" + coordinate + "}";
    }
}
