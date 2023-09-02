package com.ponderaAssembly.core.plugin;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Each class marked with this annotation will be identified as plugin within the "PonderaAssembly"
 * framework and managed by it.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Plugin {

    /**
     * Specifies a unique identifier for the plugin. <br>
     * Unique identifier for the plugin is class full name.
     *
     * @return The ID that uniquely identifies the plugin.
     */
    String id();
}
