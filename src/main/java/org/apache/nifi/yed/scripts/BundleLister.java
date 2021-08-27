package org.apache.nifi.yed.scripts;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import java.util.Set;

public class BundleLister {
    //  #
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        if(args.length != 4) {
            System.out.println("script usage :  -I <instance_folder> -y <output_file>");
            System.exit(-1);
        }

        String instanceFolder = "/opt/nifi-1.13.2";
        if (args[0].equals("-I")) {
            instanceFolder = args[1];
        }

        String destinationFile = System.getenv("HOME") + "/nifi-processors.graphml";
        if(args[2].equals("-y")){
            destinationFile = args[3];
        }

        final String propertiesFile = instanceFolder + "/conf/nifi.properties";
        final File bootstrapDirectory = new File(instanceFolder + "/lib/bootstrap/");
        final File nifiLibsDirectory = new File(instanceFolder + "/lib/");
        final File extensionDirectory = new File(instanceFolder + "/work/nar/extensions/");
        final File frameworkDirectory = new File(instanceFolder + "/work/nar/framework/");

        if (!bootstrapDirectory.exists()) {
            System.err.println("Nifi Instance folder not exists");
            System.exit(-1);
        }


        Properties propertiesNifi = new Properties();
        try (FileInputStream inputStream = new FileInputStream(propertiesFile)) {
            propertiesNifi.load(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }


        // bootstrap
        URL[] bootstrapJars = BundleManager.findAllJars(bootstrapDirectory);
        URLClassLoader bootstrapClassLoader = new URLClassLoader(bootstrapJars, ClassLoader.getSystemClassLoader());

        // nifi instance
        URL[] instanceJars = BundleManager.findAllJars(nifiLibsDirectory);
        URLClassLoader instanceClassLoader = new URLClassLoader(instanceJars, bootstrapClassLoader);


        // bundle manager
        BundleManager bundleManager = new BundleManager(instanceClassLoader, extensionDirectory, frameworkDirectory);
        Set<Bundle> bundles = bundleManager.getBundles();

        ExtensionManager extensionManager = new ExtensionManager();
        extensionManager.registerBundles(bundles);

        /**
         * print processes list into graphml file
         */
        GraphmlWriter.write(extensionManager, destinationFile);

    }


}
