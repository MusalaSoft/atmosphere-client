// This file is part of the ATMOSPHERE mobile testing framework.
// Copyright (C) 2016 MusalaSoft
//
// ATMOSPHERE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// ATMOSPHERE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with ATMOSPHERE.  If not, see <http://www.gnu.org/licenses/>.

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
