package com.musala.atmosphere.client.util.settings;

import com.musala.atmosphere.client.UiElement;

/**
 * This enum is used to define how the validity of an {@link UiElement} should be done - automatically or manual.
 * 
 * @author vladimir.vladimirov
 * 
 */
public enum ElementValidationType {
    /**
     * The check for validity of {@link UiElement} is done automatically before every operation on it. This option might
     * make tests with lot of screen manipulation run slower.
     */
    ALWAYS,

    /**
     * The check for validity of {@link UiElement} is done manually by the QA. If this option is selected, the tests
     * with lot of screen manipulation operations will run faster, but with increased probability of throwing an
     * exception if the element is invalid.
     */
    MANUAL;
}
