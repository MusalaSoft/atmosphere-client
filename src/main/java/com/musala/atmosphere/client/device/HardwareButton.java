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

package com.musala.atmosphere.client.device;

/**
 * Enumerates the most commonly used Android device hardware buttons.
 * 
 * @author georgi.gaydarov
 * 
 */
public enum HardwareButton {
    /**
     * Power key (KEYCODE_POWER, 0x1a).
     */
    POWER(26),
    /**
     * Volume-up key (KEYCODE_VOLUME_UP, 0x18).
     */
    VOLUME_UP(24),
    /**
     * Volume-down key (KEYCODE_VOLUME_DOWN, 0x19).
     */
    VOLUME_DOWN(25),
    /**
     * Menu key (KEYCODE_MENU, 0x52).
     */
    MENU(82),
    /**
     * Search key (KEYCODE_SEARCH, 0x54).
     */
    SEARCH(84),
    /**
     * Back key (KEYCODE_BACK, 0x4).
     */
    BACK(4),
    /**
     * Home key (KEYCODE_HOME, 0x3, never delivered to applications).
     */
    HOME(3),
    /**
     * Call key (KEYCODE_CALL, 0x5).
     */
    ANSWER(5),
    /**
     * End call key (KEYCODE_ENDCALL, 0x6).
     */
    DECLINE(6),
    /**
     * Camera key (KEYCODE_CAMERA, 0x1b).
     */
    CAMERA(27);

    private int code;

    private HardwareButton(int code) {
        this.code = code;
    }

    /**
     * @return the keycode for the current {@link HardwareButton HardwareButton}.
     */
    public int getKeycode() {
        return code;
    }
}
