package com.ponderaAssembly.core.runtime;

import com.ponderaAssembly.core.data.BasicDependencyFactory;
import com.ponderaAssembly.core.data.PluginA;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import static com.ponderaAssembly.core.data.BasicDependencyFactory.A_VALUE;

/**
 * Basic E2E test.
 */
public class PonderaAssemblyTest {

    /**
     * Test that application starts without error, plugin is created and initialized with its dependencies.
     */
    @Test
    public void testBasicUsage() {
        String dependencyClasses[] = {BasicDependencyFactory.class.getName()};
        String pluginClasses[] = {PluginA.class.getName()};

        Assertions.assertDoesNotThrow(() -> new PonderaMicrokernel(dependencyClasses, pluginClasses));
        Object instance = PluginRegistry.INSTANCE.get(PluginA.class.getName());
        Assertions.assertNotNull(instance);
        Assertions.assertEquals(A_VALUE,  ((PluginA) instance).getValue());
    }
}
