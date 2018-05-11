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

package com.musala.atmosphere.client;

import java.io.File;

/**
 * Contains all resource files' location.
 *
 * @author yordan.petrov
 *
 */
public class TestResources {
    public static final String RESOURCES_LOCATION = "src/test/resources" + File.separator;

    public static final String SCREENSHOT_LANDSCAPE_PATH = RESOURCES_LOCATION + "testLandscapeImage.png";

    public static final String SCREENSHOT_UPSIDE_DOWN_LANDSCAPE_PATH = RESOURCES_LOCATION
            + "testUpsideDownLandscape.png";

    public static final String SCREENSHOT_PORTRAIT_PATH = RESOURCES_LOCATION + "testPortraitImage.png";

    public static final String SCREENSHOT_UPSIDE_DOWN_PORTRAIT_PATH = RESOURCES_LOCATION + "testUpsideDownPortrait.png";

    public static final String ELEMENT_LANDSCAPE_PATH = RESOURCES_LOCATION + "testCropImageLandscape.png";

    public static final String ELEMENT_UPSIDE_DOWN_LANDSCAPE_PATH = RESOURCES_LOCATION
            + "testCropImageUpsideDownLandscape.png";

    public static final String ELEMENT_PORTRAIT_PATH = RESOURCES_LOCATION + "testCropImagePortrait.png";

    public static final String ELEMENT_UPSIDE_DOWN_PORTRAIT_PATH = RESOURCES_LOCATION
            + "testCropImageUpsideDownPortrait.png";

    public static final String IMAGE_PATH = RESOURCES_LOCATION + "testImage.png";

    public static final String IMAGE_ELEMENT_PATH = RESOURCES_LOCATION + "testImageElement.png";

    public static final String SCREENSHOT_IMAGE_PATH = RESOURCES_LOCATION + "screenshot.png";

    public static final String WRONG_PATH = "UnexistentImage.png";

    public static final String SAVED_IMAGE_NAME = RESOURCES_LOCATION + "saveTestImage.png";

    public static final String PATH_TO_APK_FILE = RESOURCES_LOCATION + "dummy-apk.apk";

    public static final String PATH_TO_NOT_EXISTING_APK_FILE = RESOURCES_LOCATION + "NotExistingFile.apk";
}
