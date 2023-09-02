package com.ponderaAssembly.core.data;

import com.ponderaAssembly.core.plugin.Plugin;
import com.ponderaAssembly.core.plugin.dependency.Dependency;

@Plugin(id= "com.ponderaAssembly.core.data.PluginA")
public class PluginA {
    private String value;

    public PluginA(@Dependency(name = "aValue") String aValue) {
        this.value = aValue;
    }

    public String getValue() {
        return this.value;

    }
}
