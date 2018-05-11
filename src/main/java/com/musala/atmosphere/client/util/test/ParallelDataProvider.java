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

package com.musala.atmosphere.client.util.test;

import java.util.ArrayList;
import java.util.List;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;

import com.musala.atmosphere.commons.cs.deviceselection.DeviceSelector;

/**
 * An utility class that uses TestNG framework to implement a parallelization logic.
 *
 * @author dimcho.nedev
 */
public abstract class ParallelDataProvider {
    private static List<DeviceSelector> deviceSelectorList;

    static {
        deviceSelectorList = new ArrayList<>();
    }

    /**
     * Here the client should initializes and add the selectors to the device selector list.
     */
    @BeforeClass
    public abstract void setUp();

    /**
     * Transforms the device selectors in convenient way to be used with dataProvider annotation of TestNG.
     *
     * @return a table of devices with parameters
     */
    @DataProvider(parallel = true)
    public static DeviceSelector[][] getData() {
        DeviceSelector[][] deviceSelectors = new DeviceSelector[deviceSelectorList.size()][1];

        for (int i = 0; i < deviceSelectorList.size(); i++) {
            deviceSelectors[i][0] = deviceSelectorList.get(i);
        }

        return deviceSelectors;
    }

    /**
     * Add a device selector to the device selector list.
     *
     * @param selector
     *        a {@link DeviceSelector} object
     */
    protected void addSelector(DeviceSelector selector) {
        deviceSelectorList.add(selector);
    }

}
