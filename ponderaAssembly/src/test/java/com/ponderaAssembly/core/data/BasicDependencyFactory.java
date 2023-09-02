package com.ponderaAssembly.core.data;

import com.ponderaAssembly.core.plugin.dependency.DependencyFactory;
import com.ponderaAssembly.core.plugin.dependency.DependencyInstance;

@DependencyFactory
public class BasicDependencyFactory {

    public static String A_VALUE = "A";
    @DependencyInstance
    public String aValue() {
        return A_VALUE;
    }
}
