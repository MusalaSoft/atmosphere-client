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

package com.musala.atmosphere.client.uiutils;

import java.io.File;
import java.util.List;

import com.musala.atmosphere.client.Builder;
import com.musala.atmosphere.client.Device;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;
import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelectorBuilder;
import com.musala.atmosphere.commons.exceptions.DeviceNotFoundException;
import com.musala.atmosphere.commons.geometry.Point;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Class responsible for the communication between the atmosphere-client and the atmosphere-viewer.
 * 
 * @author yavor.stankov
 *
 */
public class ViewerCommunicator {
    private static final String UI_DUMP_LOCAL_DIR = System.getProperty("user.dir");

    private static final String UI_DUMP_DIRECTORY_NAME = "uidump";

    private Device device;

    private static Builder builder;

    public ViewerCommunicator() {
        builder = Builder.getInstance();
    }

    /**
     * Gets the UIAutomator UI XML dump by given device serial number and saves it in a UIX file.
     * 
     * @param deviceSerialNumber
     *        - the serial number of the device
     * @return the location of the UIX file
     */
    public String getUiHierarchy(String deviceSerialNumber) {
        String xmlFilePath = String.format("%s%s%s.uix",
                                           UI_DUMP_LOCAL_DIR + File.separator,
                                           UI_DUMP_DIRECTORY_NAME + File.separator,
                                           deviceSerialNumber);
        getDevice(deviceSerialNumber);

        createDumpDirectory();
        device.getUiXml(xmlFilePath);

        releaseDevice();

        return xmlFilePath;
    }

    /**
     * Gets screenshot of this device's active screen by given device serial number and saves it in a PNG file.
     * 
     * @param deviceSerialNumber
     *        - the serial number of the device
     * @return the location of the PNG file
     */
    public String getScreenshot(String deviceSerialNumber) {
        String screenshotFilePath = String.format("%s%s%s.png",
                                                  UI_DUMP_LOCAL_DIR + File.separator,
                                                  UI_DUMP_DIRECTORY_NAME + File.separator,
                                                  deviceSerialNumber);

        getDevice(deviceSerialNumber);

        createDumpDirectory();
        device.getScreenshot(screenshotFilePath);

        releaseDevice();

        return screenshotFilePath;
    }

    public void tapScreen(String deviceSerialNumber, int x, int y) {
        getDevice(deviceSerialNumber);

        Point tapPoint = new Point(x, y);
        device.tapScreenLocation(tapPoint);

        releaseDevice();
    }

    /**
     * Gets the list with all available devices.
     * 
     * @return list with all available devices
     */
    public List<Pair<String, String>> getAvailableDevices() {
        return builder.getAllAvailableDevices();
    }

    private void getDevice(String deviceSN) {
        DeviceSelectorBuilder deviceSelectorBuilder = new DeviceSelectorBuilder().serialNumber(deviceSN);
        DeviceSelector deviceSelector = deviceSelectorBuilder.build();

        device = builder.getDevice(deviceSelector);
    }

    private void releaseDevice() {
        try {
            builder.releaseDevice(device);
        } catch (DeviceNotFoundException e) {

        }
    }

    private void createDumpDirectory() {
        String pathToDumpDirectory = String.format("%s%s", UI_DUMP_LOCAL_DIR + File.separator, UI_DUMP_DIRECTORY_NAME);
        File dumpDirectory = new File(pathToDumpDirectory);

        if (!dumpDirectory.exists()) {
            dumpDirectory.mkdirs();
        }
    }
}
