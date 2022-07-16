/*
 * (C) Copyright 2022 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import org.openqa.selenium.Capabilities;

/**
 * Avoid capabilities hardcoded in code, use external sources!
 * <ul>
 *     <li>json</li>
 *     <li>yaml</li>
 *     <li>txt</li>
 *     <li>database?</li>
 *     <li>anything you can imagine</li>
 * </ul>
 * <h3>DO. NOT. HARDCODE. CAPABILITIES.</h3>
 *
 * @param <C> any descendant of {@link Capabilities} class.
 * @author rev1an (Sergey Alekseev)
 */
public interface CapabilityProvider<C extends Capabilities> {

    /**
     * Do a magic, and return desired capabilities.
     *
     * @return new instance of {@link Capabilities} class
     */
    C get();

}
