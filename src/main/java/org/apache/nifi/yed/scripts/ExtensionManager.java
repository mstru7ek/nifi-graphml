package org.apache.nifi.yed.scripts;

import org.apache.nifi.components.ConfigurableComponent;
import org.apache.nifi.processor.Processor;

import java.util.*;

public class ExtensionManager {

    private final Map<Class<?>, Set<Class<?>>> definitionMap = new HashMap<>();

    public ExtensionManager() {
        definitionMap.put(Processor.class, new HashSet<>());
    }

    public void registerBundles(Set<Bundle> bundles) {
        for (Bundle bundle : bundles) {

            ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
            Thread.currentThread().setContextClassLoader(bundle.getClassLoader());

            for (Class<?> definitionClass : definitionMap.keySet()) {
                Set<Class<?>> classes = definitionMap.get(definitionClass);

                ServiceLoader<?> serviceLoader = ServiceLoader.load(definitionClass, bundle.getClassLoader());
                for (Object o : serviceLoader) {
                    classes.add(o.getClass());
                }
            }
            if (currentClassLoader != null) {
                Thread.currentThread().setContextClassLoader(currentClassLoader);
            }
        }
    }

    public Set<Class<?>> getExtensions(Class<?> className) {
        return definitionMap.get(className);
    }

    public ConfigurableComponent getTempComponent(Class<?> extensionClass, ClassLoader bundleClassLoader) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        ProcessMockProcessorInitializationContext initializationContext = new ProcessMockProcessorInitializationContext();
        try {
            Thread.currentThread().setContextClassLoader(bundleClassLoader);
            Processor processor = (Processor) bundleClassLoader.loadClass(extensionClass.getCanonicalName()).newInstance();
            processor.initialize(initializationContext);
            return processor;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            if (contextClassLoader != null) {
                Thread.currentThread().setContextClassLoader(contextClassLoader);
            }
        }
    }
}
