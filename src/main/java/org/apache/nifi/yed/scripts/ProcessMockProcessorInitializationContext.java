package org.apache.nifi.yed.scripts;

import org.apache.nifi.controller.ControllerServiceLookup;
import org.apache.nifi.controller.NodeTypeProvider;
import org.apache.nifi.documentation.init.NopComponentLog;
import org.apache.nifi.logging.ComponentLog;
import org.apache.nifi.processor.ProcessorInitializationContext;

import java.io.File;

public class ProcessMockProcessorInitializationContext implements ProcessorInitializationContext {

    @Override
    public String getIdentifier() {
        return "";
    }

    @Override
    public ComponentLog getLogger() {
        return new NopComponentLog();
    }

    @Override
    public ControllerServiceLookup getControllerServiceLookup() {
        return new MockControllerServiceLookup();
    }

    @Override
    public NodeTypeProvider getNodeTypeProvider() {
        return null;
    }

    @Override
    public String getKerberosServicePrincipal() {
        return null;
    }

    @Override
    public File getKerberosServiceKeytab() {
        return null;
    }

    @Override
    public File getKerberosConfigurationFile() {
        return null;
    }

}
