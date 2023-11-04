package io.github.ivangavlik.PonderaAssembly.runtime;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import io.github.ivangavlik.PonderaAssembly.plugin.*;
import io.github.ivangavlik.PonderaAssembly.plugin.dependency.Dependency;
import io.github.ivangavlik.PonderaAssembly.plugin.event.HandleEvent;
import io.github.ivangavlik.PonderaAssembly.plugin.extension.ExtensionPoint;
import io.github.ivangavlik.PonderaAssembly.plugin.hook.Start;
import io.github.ivangavlik.PonderaAssembly.runtime.event.PluginEventManager;

/**
 * The central class that serves as the microkernel for the PonderaAssembly plugin framework.
 * It handles the loading, initialization, and coordination of plugins.
 */
public final class PonderaMicrokernel {
    public static PluginEventManager EVENT = new PluginEventManager(PluginRegistry.INSTANCE.getInstances());
    private PluginRegistry pluginRegistry = PluginRegistry.INSTANCE;

    /**
     * Initializes the PonderaMicrokernel by loading dependencies and plugins,
     *
     * <p> Initialization steps </p>
     * <ol>
     *  <li> init all dependencies </li>
     *  <li> call plugins constructor</li>
     *  <li> init extension points </li>
     *  <li> run start method</li>
     * </ol>
     *
     * @param dependencyClasses An array of dependency class names.
     * @param pluginClasses An array of plugin class names.
     * */
    // TODO cover the case were start is called before extended
    public PonderaMicrokernel(final String[] dependencyClasses, final String[] pluginClasses) {
        // load dependencies
        StartUpHelper.loadDependencies(dependencyClasses)
                .stream()
                .forEach(pair -> {
                    pluginRegistry.addInstance(pair.getName(), pair.getInstance());
                });

        // run plugin constructor
        StartUpHelper.loadPlugins(pluginClasses)
                .stream()
                .filter(pluginClass ->  pluginRegistry.exist(pluginClass.getName()))
                .map(pluginClass -> runConstructor(pluginClass))
                .forEach(instance ->  pluginRegistry.addInstance(instance.getClass().getName(), instance));

        // TODO order is not handled
        pluginRegistry.getInstances().values().stream()
                .forEach(instances -> initExtensionPoint(instances));

        // TODO order is not handled - should I use tree set
        pluginRegistry.getInstances().values().stream()
                .filter(el -> el.getClass().getDeclaredAnnotation(Plugin.class) != null)
                .forEach(in -> runPluginStart(in));

        List<Object> actionHandlers = pluginRegistry.getInstances().values().stream()
                .filter(el -> el.getClass().getDeclaredAnnotation(Plugin.class) != null)
                .filter(el -> Arrays.stream(el.getClass().getDeclaredMethods())
                        .anyMatch(method ->  method.isAnnotationPresent(HandleEvent.class)))
                .collect(Collectors.toList());

        EVENT.pluginActionList.addAll(actionHandlers);
    }
    private Object runConstructor(Class pluginClass) {
        for (int i = 0; i < pluginClass.getDeclaredConstructors().length; i++) {
            Constructor con = pluginClass.getDeclaredConstructors()[i];

            Object[] params = Arrays.stream(con.getParameters())
                    .filter(el -> el.isAnnotationPresent(Dependency.class))
                    .map(el ->  pluginRegistry.get(el.getAnnotation(Dependency.class).name()))
                    .toArray(Object[]::new);
            try {
                return con.newInstance(params);
            } catch (Exception exception) {
                throw new RuntimeException(exception);
            }
        }
        try {
            // if no declared use default constructor
            return pluginClass.getConstructors()[0].newInstance();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
    private void runPluginStart(Object instance) {
        Arrays.stream(instance.getClass().getDeclaredMethods())
                .map(method -> new Object() {
                            Method classMethod = method;
                            Annotation annotation = method.getAnnotation(Start.class);
                        }
                )
                .filter(el -> el.annotation != null)
                .map(el -> el.classMethod)
                .findFirst()
                .ifPresent(el -> {
                    try {
                        el.invoke(instance); // TODO method params
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    } catch (InvocationTargetException e) {
                        throw new RuntimeException(e);
                    }
                });

    }
    private Object initExtensionPoint(Object instance) {
        Arrays.stream(instance.getClass().getDeclaredFields())
                .filter(el -> el.getAnnotation(ExtensionPoint.class) != null)
                .map(el -> {
                    // get classes that implements this extension points class
                    List<Class> allImpl = this.pluginRegistry.getInstances().values()
                            .stream()
                            .map(in -> in.getClass())
                            .map(in -> new Object() {
                                        Class classInfo = in;
                                        List interfaces = Arrays.stream(in.getInterfaces()).collect(Collectors.toList());
                                    }
                            )
                            // contains because typeName includes List<Type>
                            .filter(in -> in.interfaces.stream().anyMatch(i -> el.getGenericType().getTypeName().contains(((Class) i).getName())))
                            .map(in -> in.classInfo)
                            .collect(Collectors.toList());

                    // for each class create instance and add it to instances
                    List<Object> values = allImpl
                            .stream()
                            .map(item -> {
                                Object value =  this.pluginRegistry.get(item.getName());
                                // find class that implements specific extension point create it and assign
                                // it to this instance
                                if (value != null) {
                                    return value;
                                }
                                Object instanceNew = runConstructor(item);
                                pluginRegistry.addInstance(instanceNew.getClass().getName(), instanceNew);
                                return instanceNew;

                            })
                            .collect(Collectors.toList());

                    // set up instance extension value
                    try {
                        el.setAccessible(true);
                        el.set(instance, values);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    return el;
                })
                .collect(Collectors.toList());

        return instance;
    }
}
