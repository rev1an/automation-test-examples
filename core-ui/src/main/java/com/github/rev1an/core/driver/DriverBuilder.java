/*
 * Copyright (c) 2023 Sergey Alekseev (https://github.com/rev1an).
 *
 * Software distributed under MIT license.
 */

package com.github.rev1an.core.driver;

import org.openqa.selenium.WebDriver;

/**
 * Implementations of this class <b>must</b> setup and return instance of {@link WebDriver} class.
 * <p>
 * Not more, not less.
 *
 * @param <WD> any descendant or wrapper of {@link WebDriver} class.
 * @author rev1an (Sergey Alekseev)
 */
public interface DriverBuilder<WD extends WebDriver> {

    /**
     * @return new instance of {@link WebDriver} class
     */
    WD build();

}
