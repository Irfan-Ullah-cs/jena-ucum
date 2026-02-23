package org.lindt.ucum;

import org.apache.jena.sys.JenaSubsystemLifecycle;

/**
 * Auto-initialization via Java ServiceLoader.
 * 
 * When this JAR is on the classpath, Jena automatically discovers
 * this class via META-INF/services and calls start() during initialization.
 * This replaces the need to modify TypeMapper.java.
 */
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
