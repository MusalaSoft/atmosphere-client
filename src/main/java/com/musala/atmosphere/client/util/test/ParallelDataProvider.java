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
