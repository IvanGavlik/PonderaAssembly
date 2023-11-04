package io.github.ivangavlik.PonderaAssembly.runtime.event;

import io.github.ivangavlik.PonderaAssembly.plugin.event.HandleEvent;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * Manages the event-driven communication between plugins in the framework.
 */
public final class PluginEventManager {
    private final Map<String, Object> instances;
    public final List<Object> pluginActionList = new ArrayList<>();

    /**
     * Constructs a PluginEventManager instance with a map of instances.
     *
     * @param instances A map containing instances.
     */public PluginEventManager(Map<String, Object> instances) {
        this.instances = instances;
    }

    /**
     * Emits an event to trigger event handlers across plugins.
     * Event handler has to be marked with {@link io.github.ivangavlik.PonderaAssembly.plugin.event.HandleEvent} annotation.
     *
     * @param payload The payload to be passed to event handler.
     */
    public void emit(Object payload) {
        pluginActionList.stream()
                .map(el -> el.getClass())
                .forEach(el -> {
                    Arrays.stream(el.getMethods())
                            .filter(method -> method.getDeclaredAnnotation(HandleEvent.class) != null)
                            .forEach(method -> {
                                try {
                                    method.invoke(instances.get(el.getName()), payload);
                                } catch (IllegalAccessException e) {
                                    throw new RuntimeException(e);
                                } catch (InvocationTargetException e) {
                                    throw new RuntimeException(e);
                                }
                            });
                });
    }

}
