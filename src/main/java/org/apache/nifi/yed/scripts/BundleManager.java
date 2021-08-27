package org.apache.nifi.yed.scripts;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

public class BundleManager {

    private final ClassLoader systemClassLoader;

    private Map<String, Bundle> bundleCoordinatesToBundle = new LinkedHashMap<>();

    public BundleManager(ClassLoader systemClassLoader, File extensionFolder, File frameworkFolder) {
        this.systemClassLoader = systemClassLoader;

        loadBundlesDefinitions(extensionFolder);
        loadBundlesDefinitions(frameworkFolder);

        updateWithEmptyClassLoader();
    }

    private void loadBundlesDefinitions(File directory) {

        String[] narDirectories = directory.list((dir, name) -> name.endsWith("-unpacked"));

        for (String narDirectory : narDirectories) {
            buildNarClassLoader(new File(directory, narDirectory));
        }

    }

    private void buildNarClassLoader(File workingDirectory) {

        final Bundle.Builder builder = new Bundle.Builder();
        builder.workingDirectory = workingDirectory;

        final File manifestFile = new File(workingDirectory, "META-INF/MANIFEST.MF");
        try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(manifestFile))) {

            Manifest manifest = new Manifest(inputStream);
            Attributes attributes = manifest.getMainAttributes();

            String narGroup = attributes.getValue(NarManifestEntry.NAR_GROUP.attr);
            String narId = attributes.getValue(NarManifestEntry.NAR_ID.attr);
            String narVersion = attributes.getValue(NarManifestEntry.NAR_VERSION.attr);
            builder.bundleDescriptor = new BundleDescriptor(narGroup, narId, narVersion);

            String dependencyGroup = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_GROUP.attr);
            String dependencyId = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_ID.attr);
            String dependencyVersion = attributes.getValue(NarManifestEntry.NAR_DEPENDENCY_VERSION.attr);

            if (dependencyId != null && !dependencyId.isEmpty()) {
                builder.dependencyDescriptor = new BundleDescriptor(dependencyGroup, dependencyId, dependencyVersion);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }


        // fetch all bundle contained jars
        File bundleDependencies = new File(workingDirectory, "NAR-INF/bundled-dependencies");
        builder.bundleJars = findAllJars(bundleDependencies);


        final String bundleCoordinate = builder.bundleDescriptor.getCoordinate();

        if (builder.dependencyDescriptor == null) {
            builder.classLoader = new NarClassLoader(bundleCoordinate, builder.bundleJars, systemClassLoader);
        } else {
            Bundle dependencyBundle = bundleCoordinatesToBundle.get(builder.dependencyDescriptor.getCoordinate());
            if (dependencyBundle != null && dependencyBundle.getClassLoader() != null) {
                builder.classLoader = new NarClassLoader(bundleCoordinate, builder.bundleJars, dependencyBundle.getClassLoader());
            }
        }
        Bundle bundle = builder.build();

        bundleCoordinatesToBundle.put(bundle.getBundleDescriptor().getCoordinate(), bundle);
    }

    public static URL[] findAllJars(File bundleDependencies) {
        try {
            return Files.walk(bundleDependencies.toPath()).filter(path -> path.toString().endsWith(".jar")).map(BundleManager::toUri).toArray(URL[]::new);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public static URL toUri(Path path) {
        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    private void updateWithEmptyClassLoader() {
        for (String bundleCoordinate : bundleCoordinatesToBundle.keySet()) {
            initBundleClassLoader(bundleCoordinate);
        }
    }

    private void initBundleClassLoader(String bundleCoordinate) {

        Bundle bundle = bundleCoordinatesToBundle.get(bundleCoordinate);

        if (bundle == null) {
            System.out.println("bundle not found [ un-initialized classLoader  ] : " + bundleCoordinate);
            return;
        }

        if (bundle.getClassLoader() == null) {

            initBundleClassLoader(bundle.getDependencyDescriptor().getCoordinate());
            Bundle dependencyBundle = bundleCoordinatesToBundle.get(bundle.getDependencyDescriptor().getCoordinate());

            Bundle.Builder builder = bundle.with();
            builder.classLoader = new NarClassLoader(bundleCoordinate, bundle.getBundleJars(), dependencyBundle.getClassLoader());
            Bundle updatedBundle = builder.build();

            bundleCoordinatesToBundle.put(bundle.getBundleDescriptor().getCoordinate(), updatedBundle);
        }
    }

    public Set<Bundle> getBundles() {
        return new HashSet<>(bundleCoordinatesToBundle.values());
    }
}
