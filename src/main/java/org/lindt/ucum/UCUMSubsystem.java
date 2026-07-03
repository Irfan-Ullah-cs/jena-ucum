package org.lindt.ucum;

import org.apache.jena.sys.JenaSubsystemLifecycle;

public class UCUMSubsystem implements JenaSubsystemLifecycle {

    @Override
    public void start() {
        UCUMConfig.init();
    }

    @Override
    public void stop() {
    }

    @Override
    public int level() {
        return 500;  // After Jena core (42) but before user code
    }
}
