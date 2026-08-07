package model.core;

import java.util.*;
import java.util.function.Consumer;

public class EventBus {
    private Map<Class<?>, List<Consumer<Object>>> subscribers = new HashMap<>();

    @SuppressWarnings("unchecked") // This is actually safe since we select based on class when publishing
    public <T> void subscribe(Class<T> eventType, Consumer<T> handler) {
        subscribers.computeIfAbsent(eventType, k -> new ArrayList<>())
                .add(event -> handler.accept((T) event));
    }

    public void publish(Object event) {
        List<Consumer<Object>> handlers = subscribers.get(event.getClass());
        if (handlers != null) {
            for (Consumer<Object> handler : handlers) {
                handler.accept(event);
            }
        }
    }
}
