/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver.capabilities.deserializer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.ParameterizedType;
import java.util.Map;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.github.rev1an.core.json.TypeReferences;
import org.openqa.selenium.remote.AbstractDriverOptions;

/**
 * Abstraction that defines a process of creating {@link AbstractDriverOptions} instances.
 * <p>
 * Descendants of this class must provide:
 * <ul>
 *     <li>
 *         Generic type argument {@code T} of desired {@code ***Options} class.
 *     </li>
 *     <li>
 *         Implement {@link #processCapabilities(AbstractDriverOptions, Map)} method, initialize empty capabilities
 *         from raw {@link Map}.
 *     </li>
 * </ul>
 *
 * @param <T> any descendant of {@link AbstractDriverOptions} class.
 * @author rev1an (Sergey Alekseev)
 */
@SuppressWarnings("unchecked")
abstract class AbstractDriverOptionsDeserializer<T extends AbstractDriverOptions<?>> extends JsonDeserializer<T> {

    /**
     * <ul>
     *     <li>
     *         Create empty instance of generic argument {@code T}.
     *     </li>
     *     <li>
     *         Read {@code json} file to raw {@link Map}.
     *     </li>
     *     <li>
     *         Initialize capabilities in {@link #createEmptyCapabilities()}
     *     </li>
     *     <li>
     *         Close parser, better safe than sorry.
     *     </li>
     * </ul>
     * <p>
     * Marked as <b>final</b>, no reason to override it in descendants.
     *
     * @param p    Parsed used for reading JSON content
     * @param ctxt Context that can be used to access information about
     *             this deserialization activity.
     * @return new instance of {@link AbstractDriverOptions} descendant.
     * @throws IOException      is something goes wrong while reading raw {@link Map}
     * @throws RuntimeException is something goes wrong {@link #createEmptyCapabilities()}
     */
    @Override
    public final T deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        try (final var parser = p) { // close when done
            final var capabilities = createEmptyCapabilities();
            final var raw = (Map<String, Object>) parser.readValueAs(TypeReferences.MAP_STRING_OBJECT);
            processCapabilities(capabilities, raw);
            return capabilities;
        }
    }

    /**
     * Each implementation of {@link AbstractDriverOptions} have own unique capability setters.
     * <p>
     * Thus, implementations of {@link AbstractDriverOptionsDeserializer} must provide this logic.
     *
     * @param capabilities instance of {@code T} capabilities
     * @param key          capability key
     * @param value        capability value
     */
    protected abstract void setCapability(T capabilities, String key, Object value);

    /**
     * Traverse {@param raw} recursively and set capabilities into provided {@code T} capabilities instance.
     *
     * @param capabilities empty instance of {@code T} capabilities
     * @param raw          capabilities from {@code json} file
     */
    private void processCapabilities(T capabilities, Map<String, Object> raw) {
        for (final var entry : raw.entrySet()) {
            final var value = entry.getValue();
            if (value instanceof Map) {
                processCapabilities(capabilities, (Map<String, Object>) value);
            }
            setCapability(capabilities, entry.getKey(), value);
        }
    }

    /**
     * Create an empty instance of any {@link AbstractDriverOptions}, based on
     * generic type parameter provided to descendant of this class.
     *
     * @return new instance of {@code T} provided as generic argument.
     * @throws RuntimeException in a bunch of {@link ReflectiveOperationException} cases
     */
    private T createEmptyCapabilities() {
        final var clazz = getGenericType();
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot create instance ot type: %s".formatted(clazz), e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot access default empty constructor in type: %s".formatted(clazz), e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Exception calling default empty constructor in type: %s".formatted(clazz), e);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Unable to find default empty constructor in type: %s".formatted(clazz), e);
        }
    }

    /**
     * A trick from old times: <a href="http://gafter.blogspot.com/2006/12/super-type-tokens.html">Super Type Tokens</a>
     * <p>
     * Still works in 2022.
     *
     * @return type of generic parameter {@code T}.
     */
    private Class<T> getGenericType() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
