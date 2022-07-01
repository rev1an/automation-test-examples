/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.json;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Expanding {@link ObjectMapper} with additional behaviour.
 *
 * @author rev1an (Sergey Alekseev)
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

}
