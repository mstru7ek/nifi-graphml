package org.apache.nifi.graphml.scripts;

import org.apache.nifi.controller.ControllerService;
import org.apache.nifi.controller.ControllerServiceLookup;

import java.util.Set;

class MockControllerServiceLookup implements ControllerServiceLookup {
    @Override
    public ControllerService getControllerService(String s) {
        return null;
    }

    @Override
    public boolean isControllerServiceEnabled(String s) {
        return false;
    }

    @Override
    public boolean isControllerServiceEnabling(String s) {
        return false;
    }

    @Override
    public boolean isControllerServiceEnabled(ControllerService controllerService) {
        return false;
    }

    @Override
    public Set<String> getControllerServiceIdentifiers(Class<? extends ControllerService> aClass) throws IllegalArgumentException {
        return null;
    }

    @Override
    public String getControllerServiceName(String s) {
        return null;
    }
}
