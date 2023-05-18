/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.json;

import java.io.UncheckedIOException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Expanding {@link ObjectMapper} with additional behaviour.
 */
public class JacksonMapper extends ObjectMapper {

    /**
     * {@inheritDoc}
     */
    public JacksonMapper() {
        super();
    }

    /**
     * Support {@link #copy()} method
     *
     * @see ObjectMapper#ObjectMapper(ObjectMapper)
     */
    public JacksonMapper(JacksonMapper src) {
        super(src);
    }

    @Override
    public JacksonMapper copy() {
        return new JacksonMapper(this);
    }

    public JsonNode asJsonNode(String content) {
        try {
            return super.readTree(content);
        } catch (JsonProcessingException e) {
            throw new UncheckedIOException(e);
        }
    }

}
