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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;

/**
 * Class for interactions with time picker widget.
 * 
 * @author filareta.yordanova
 * 
 */

public class TimePicker extends PickerView {

    private static final String TIME_FORMAT = "hh:mm a";

    private static final String TIME_24_HOURS_FORMAT = "hh:mm";

    private static final int HOUR_INDEX = 0;

    private static final int MINUTE_INDEX = 2;

    private static final int MERIDIEM_INDEX = 1;

    private static final String GENERAL_TIME_FORMATTER = "%s:%s";

    private static final String TIME_FORMATTER_WITH_MERIDIEM = "%s:%s %s";

    private static final String AM = "AM";

    private static final String PM = "PM";

    private PickerHelper pickerHelper;

    public TimePicker(Screen screen) {
        super(screen);
        pickerHelper = new PickerHelper(screen);
    }

    @Override
    public boolean setValue(Calendar value) throws MultipleElementsFoundException, UiElementFetchingException {
        boolean is24HourFormat = false;

        try {
            pickerHelper.getNumberPickerFieldValue(MERIDIEM_INDEX);
        } catch (UiElementFetchingException e) {
            is24HourFormat = true;
        }

        String minute = String.valueOf(value.get(Calendar.MINUTE));
        String hour = String.valueOf(value.get(Calendar.HOUR));

        setText(minute, MINUTE_INDEX);

        if (is24HourFormat) {
            hour = String.valueOf(value.get(Calendar.HOUR_OF_DAY));
            setText(hour, HOUR_INDEX);
        } else {
            setText(hour, HOUR_INDEX);
            String meridiem = value.get(Calendar.AM_PM) == Calendar.AM ? AM : PM;
            setText(meridiem, MERIDIEM_INDEX);
        }

        return true;
    }

    @Override
    public Calendar getValue() throws MultipleElementsFoundException, UiElementFetchingException {
        String response = getStringValue();
        Date parsedTime = null;
        parsedTime = parseTime(response, TIME_FORMAT);

        if (parsedTime == null) {
            parsedTime = parseTime(response, TIME_24_HOURS_FORMAT);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedTime);

        return calendar;
    }

    /**
     * Sets text in a picker editText field.
     * 
     * @param value
     *        - the text to input.
     * @param pickerIndex
     *        - the index of the given picker editText field.
     * @return <code>true</code> if the method succeed, <code>false</code> if it fails.
     * @throws UiElementFetchingException
     *         if no elements are found for the passed query
     * @throws MultipleElementsFoundException
     *         if more than one element is found for the given pickerIndex
     */
    public boolean setText(String value, int pickerIndex)
        throws MultipleElementsFoundException,
            UiElementFetchingException {
        String currentValue = pickerHelper.getNumberPickerFieldValue(pickerIndex);

        if (!value.equals(currentValue)) {
            if (!pickerHelper.setTextInNumberPickerField(pickerIndex, value)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Checks is the given time in the appropriate format.
     * 
     * @param time
     *        - the given time in String format.
     * @param format
     *        - the appropriate format the time should be in.
     * @return Parsed time when the time is in appropriate format, <code>null</code> when it's not.
     */
    private Date parseTime(String time, String format) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(format);
        Date parsedTime = null;
        try {
            parsedTime = timeFormat.parse(time);
        } catch (ParseException e) {
            // Time is not in the appropriate format.
        }

        return parsedTime;
    }

    @Override
    public String getStringValue() throws MultipleElementsFoundException, UiElementFetchingException {
        String meridiem = null;
        String hour = pickerHelper.getNumberPickerFieldValue(HOUR_INDEX);
        String minute = pickerHelper.getNumberPickerFieldValue(MINUTE_INDEX);
        String time = String.format(GENERAL_TIME_FORMATTER, hour, minute);

        try {
            meridiem = pickerHelper.getNumberPickerFieldValue(MERIDIEM_INDEX);
            time = String.format(TIME_FORMATTER_WITH_MERIDIEM, hour, minute, meridiem);
        } catch (UiElementFetchingException e) {
            // Time picker is in 24 hours format view and there is no number picker widget for meridiem.
        }

        return time;
    }
}
