package com.ponderaAssembly.core.runtime;



import com.ponderaAssembly.core.plugin.Plugin;
import com.ponderaAssembly.core.plugin.dependency.DependencyFactory;
import com.ponderaAssembly.core.plugin.dependency.DependencyInstance;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Utility methods to load classes and create instances (dependencies and plugins included)
 */
final class StartUpHelper {
    public static List<Pair> loadDependencies(String[] dependencyClasses) {
        return Arrays.stream(dependencyClasses)
                .map(el -> toDependencyFactory(el))
                .filter(dependencyClass -> dependencyClass.isPresent())
                .flatMap(pluginClass -> toDependencyInstance(pluginClass.get()).stream())
                .collect(Collectors.toList());
    }
    private static Optional<Class<DependencyFactory>> toDependencyFactory(String name) {
        try {
            Class candidateClass = Class.forName(name);
            Annotation annotation = candidateClass.getDeclaredAnnotation(DependencyFactory.class);
            if (annotation != null) {
                return Optional.of(candidateClass);
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    private static List<StartUpHelper.Pair> toDependencyInstance(Class dependencyClass) {
        final Optional<Object> instance = getInstance(dependencyClass);
        if (!instance.isPresent()) {
            return new ArrayList<>();
        }
        return Arrays.stream(dependencyClass.getDeclaredMethods())
                .filter(el -> el.isAnnotationPresent(DependencyInstance.class))
                .map(el -> {
                    try {
                        StartUpHelper.Pair pair = new Pair(el.getName(), el.invoke(instance.get()));
                        return Optional.of(pair);
                    } catch (Exception ex) {
                        return Optional.empty();
                    }
                })
                .filter(el -> el.isPresent())
                .map(el ->  (StartUpHelper.Pair) el.get())
                .collect(Collectors.toList());
    }
    private static Optional<Object> getInstance(Class dependencyClass) {
        try {
           return Optional.of(dependencyClass.getConstructors()[0].newInstance());
        } catch (Exception e) {
           e.printStackTrace();
           return Optional.empty();
        }
    }
    public static List<Class<Plugin>> loadPlugins(String[] pluginClasses) {
        return Arrays.stream(pluginClasses)
                .map(el -> toPlugin(el))
                .filter(pluginClass -> pluginClass.isPresent())
                .map(pluginClass -> pluginClass.get())
                .collect(Collectors.toList());
    }
    private static Optional<Class<Plugin>> toPlugin(String name) {
        try {
            Class candidateClass = Class.forName(name);
            Annotation annotation = candidateClass.getDeclaredAnnotation(Plugin.class);
            if (annotation != null) {
                return Optional.of(candidateClass);
            } else {
                return Optional.empty();
            }
        } catch (Exception ex) {
            return Optional.empty();
        }
    }
    public static class Pair {
        private String name;
        private Object instance;

        public Pair(String name, Object instance) {
            this.name = name;
            this.instance = instance;
        }
        public String getName() {
            return name;
        }
        public Object getInstance() {
            return instance;
        }
    }
}
