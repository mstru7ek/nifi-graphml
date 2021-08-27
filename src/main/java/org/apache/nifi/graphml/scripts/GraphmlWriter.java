package org.apache.nifi.graphml.scripts;



import org.apache.nifi.annotation.documentation.Tags;
import org.apache.nifi.components.ConfigurableComponent;
import org.apache.nifi.components.PropertyDescriptor;
import org.apache.nifi.processor.Processor;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.LinkedList;
import java.util.List;


public class GraphmlWriter {
    static public void write(ExtensionManager extensionManager, String destinationFile) {
        if (extensionManager == null) {
            error("extension manager out of scope");
            return;
        }

        try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(destinationFile))) {

            TemplateWriter.writeLead(outputStream);

            for (Class<?> extensionClassName : extensionManager.getExtensions(Processor.class)) {

                if (ConfigurableComponent.class.isAssignableFrom(extensionClassName)) {

                    final Class<?> classType = extensionClassName.asSubclass(ConfigurableComponent.class);
                    final ConfigurableComponent component = extensionManager.getTempComponent(extensionClassName, extensionClassName.getClassLoader());

                    if (Processor.class.isAssignableFrom(classType)) {

                        LinkedList<String> propertyList = new LinkedList<>();
                        List<PropertyDescriptor> propertyDescriptors = component.getPropertyDescriptors();

                        if (propertyDescriptors != null) {
                            for (PropertyDescriptor property : propertyDescriptors) {
                                propertyList.add("{ " + property.getName() + " }");
                            }
                        }

                        final Tags tags = component.getClass().getAnnotation(Tags.class);
                        if (tags == null) {
                            warn("No tags found for {}, skipping...", new Object[]{extensionClassName});
                            continue;
                        }

                        final String tagLine = String.join(" | ", tags.value());
                        String[] tagsList = TemplateWriter.splitText(tagLine, 56);

                        final String className = extensionClassName.getSimpleName();

                        TemplateWriter.writeProcessor(className, propertyList.toArray(new String[]{}), tagsList, outputStream);

                    }
                }
            }
            TemplateWriter.writeClose(outputStream);

        } catch (IOException | URISyntaxException e) {
            error("file not writeable", e);
        }
    }

    public static void error(String error) {
        System.err.println(error);
    }

    public static void error(String error, Throwable e) {
        System.err.println(error);
        e.printStackTrace(System.err);

    }

    public static void error(String error, Object... args) {
        System.err.print(error);
        for (Object arg : args) {
            System.err.print("" + arg);
        }
        System.err.println();
    }

    public static void warn(String error, Object... args) {
        error(error, args);
    }
}
