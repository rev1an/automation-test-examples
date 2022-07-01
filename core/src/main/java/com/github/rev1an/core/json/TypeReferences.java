/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.json;

import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * Container for Jackson {@link TypeReference}'s
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class TypeReferences {

    // @formatter:off
    public static final TypeReference<Map<String, Object>> MAP_STRING_OBJECT = new TypeReference<>(){};
    public static final TypeReference<Map<String, Map<String, Object>>> MAP_STRING_MAP = new TypeReference<>(){};
    // @formatter:on

    private TypeReferences() {
    }

}
