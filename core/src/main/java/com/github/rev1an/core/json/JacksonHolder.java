/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.json;

/**
 * Singleton wrapper around default instance of {@link JacksonMapper}
 *
 * @author rev1an (Sergey Alekseev)
 */
public final class JacksonHolder {

    /**
     * Reusable singleton instance, mostly for copying and utility actions.
     */
    public static JacksonMapper DEFAULT;

    /*
     * Global settings and modules can be configured here.
     * */
    static {
        DEFAULT = new JacksonMapper();
    }

    private JacksonHolder() {
    }

}
