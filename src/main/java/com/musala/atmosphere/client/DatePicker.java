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
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ActionFailedException;
import com.musala.atmosphere.client.exceptions.MultipleElementsFoundException;
import com.musala.atmosphere.commons.exceptions.UiElementFetchingException;

/**
 * Manages DatePicker functionality for getting and setting values and getting individual elements of the picker.
 *
 * @author denis.bialev
 *
 */
public class DatePicker extends PickerView {
    private static final Logger LOGGER = Logger.getLogger(DatePicker.class);

    public static enum Month {
        JAN(1),
        FEB(2),
        MAR(3),
        APR(4),
        MAY(5),
        JUN(6),
        JUL(7),
        AUG(8),
        SEP(9),
        OCT(10),
        NOV(11),
        DEC(12);

        private int integerRepresentation;

        private static Map<String, Integer> monthsStringToIntegerMap;

        private static Map<Integer, String> monthsIntegerToStringMap;

        /**
         * Initializes the hash maps that keep the 'integer value - string representation' relationships.
         */
        private static void constructMaps() {
            monthsStringToIntegerMap = new HashMap<>();
            monthsIntegerToStringMap = new HashMap<>();

            for (Month month : Month.values()) {
                monthsStringToIntegerMap.put(month.toString(), month.getIntegerRepresentation());
                monthsIntegerToStringMap.put(month.getIntegerRepresentation(), month.toString());
            }
        }

        private Month(int integerRepresentation) {
            this.integerRepresentation = integerRepresentation;
        }

        /**
         * Gets the integer value of the month.
         *
         * @return the integer value of the month.
         */
        public int getIntegerRepresentation() {
            return integerRepresentation;
        }

        /**
         * Gets the String value of the given month.
         *
         * @param monthIntegerValue
         *        - an integer value of the month.
         * @return String containing a 3 letter abbreviation of the month.
         */
        public static String getStringRepresentation(int monthIntegerValue) {
            if (monthsStringToIntegerMap == null || monthsIntegerToStringMap == null) {
                constructMaps();
            }
            return monthsIntegerToStringMap.get(monthIntegerValue);
        }

        /**
         * Gets the integer value of the month by given 3 letter abbreviation for a month.
         *
         * @param month
         *        - a string representation 3 letter abbreviation for a month.
         * @return the integer value of the given month.
         */
        public static Integer getIntValue(String month) {
            if (monthsStringToIntegerMap == null || monthsIntegerToStringMap == null) {
                constructMaps();
            }
            return monthsStringToIntegerMap.get(month.toUpperCase());
        }
    }

    private static final int FIRST_POSIBLE_INDEX = 0;

    private static final int LAST_POSIBLE_INDEX = 2;

    public static final String DATE_FORMAT = "dd-M-yyyy";

    private static final String DATE_FORMATTER = "%s-%s-%s";

    private static final int HIGHEST_POSSIBLE_DAY = 31;

    private int monthPickerIndex;

    private int dayPickerIndex;

    private int yearPickerIndex;

    private PickerHelper pickerHelper;

    public DatePicker(Screen screen) throws MultipleElementsFoundException, UiElementFetchingException {
        super(screen);
        pickerHelper = new PickerHelper(screen);

        evaluatePickerIndexes();
    }

    @Override
    public boolean setValue(Calendar calendar) throws MultipleElementsFoundException, UiElementFetchingException {
        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = Month.getStringRepresentation(calendar.get(Calendar.MONTH) + 1);
        boolean setTextResult = setText(year, yearPickerIndex);
        setTextResult &= setText(day, dayPickerIndex);
        setTextResult &= setText(month, monthPickerIndex);
        return setTextResult;
    }

    /**
     * Gets the year in DatePicker.
     *
     * @return - int instance of DatePicker's year field
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present
     * @throws MultipleElementsFoundException
     *         if multiple NumberPicker or matching EditText elements are present
     */
    public int getYear() throws MultipleElementsFoundException, UiElementFetchingException {
        String year = pickerHelper.getNumberPickerFieldValue(yearPickerIndex);
        int yearNumber = Integer.parseInt(year);
        return yearNumber;
    }

    /**
     * Gets the day in DatePicker.
     *
     * @return - int instance of DatePicker's day field.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws MultipleElementsFoundException
     *         if more than one NumberPicker or EditText element is present
     */
    public int getDay() throws MultipleElementsFoundException, UiElementFetchingException {
        String day = pickerHelper.getNumberPickerFieldValue(dayPickerIndex);
        int dayNumber = Integer.parseInt(day);
        return dayNumber;
    }

    /**
     * Gets the month in DatePicker.
     *
     * @return - int instance of DatePicker's month field.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws MultipleElementsFoundException
     *         if more than one NumberPicker or EditText is found
     */
    public int getMonth() throws MultipleElementsFoundException, UiElementFetchingException {
        evaluatePickerIndexes();

        String month = pickerHelper.getNumberPickerFieldValue(monthPickerIndex);
        int monthNumber = Month.getIntValue(month);
        return monthNumber;
    }

    @Override
    public Calendar getValue() throws MultipleElementsFoundException, UiElementFetchingException {

        String date;
        String month = pickerHelper.getNumberPickerFieldValue(monthPickerIndex);
        String day = pickerHelper.getNumberPickerFieldValue(dayPickerIndex);
        String year = pickerHelper.getNumberPickerFieldValue(yearPickerIndex);

        String monthNumberValue = Month.getIntValue(month).toString();
        date = String.format(DATE_FORMATTER, day, monthNumberValue, year);

        Date parsedDate;
        try {
            parsedDate = parseDate(date, DATE_FORMAT);
        } catch (ParseException e) {
            String message = String.format("Parsing the fetched from the datepicker date string %s failed. Expected format: %s.",
                                           date,
                                           DATE_FORMAT);
            LOGGER.error(message, e);
            throw new ActionFailedException(message, e);
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedDate);

        return calendar;
    }

    @Override
    public String getStringValue() throws MultipleElementsFoundException, UiElementFetchingException {

        String date;
        String month = pickerHelper.getNumberPickerFieldValue(monthPickerIndex);
        String day = pickerHelper.getNumberPickerFieldValue(dayPickerIndex);
        String year = pickerHelper.getNumberPickerFieldValue(yearPickerIndex);

        date = String.format(DATE_FORMATTER, month, day, year);
        return date;

    }

    /**
     * Finds the index of all DatePicker fields.
     *
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws MultipleElementsFoundException
     *         if more than one NumberPicker or EditText element is present
     */
    private void evaluatePickerIndexes() throws MultipleElementsFoundException, UiElementFetchingException {
        String pickerValue;

        for (int index = FIRST_POSIBLE_INDEX; index <= LAST_POSIBLE_INDEX; index++) {
            pickerValue = pickerHelper.getNumberPickerFieldValue(index);

            try {
                int pickerNumericValue = Integer.parseInt(pickerValue);

                if (pickerNumericValue > HIGHEST_POSSIBLE_DAY) {
                    yearPickerIndex = index;
                } else {
                    dayPickerIndex = index;
                }
            } catch (NumberFormatException e) {
                monthPickerIndex = index;
            }
        }
    }

    /**
     * Parses the string from getDatePicker in Date format
     *
     * @param date
     *        - string containing the date we want to parse.
     * @param format
     *        - the format in which the date will be parsed.
     * @return - Date with the parsed string in the given date format.
     * @throws ParseException
     */
    private Date parseDate(String date, String format) throws ParseException {

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date parsedDate = dateFormat.parse(date);
        return parsedDate;
    }

    /**
     * Sets the text of a NumberPicker EditText field
     *
     * @param value
     *        - the text that should be set in the EditText field
     * @param pickerInstance
     *        - the Index of the NumberPicker that contains the EditText
     * @return - true if setting of the text is successful and false otherwise.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws MultipleElementsFoundException
     *         if more than one NumberPicker or EditText fields are found
     */
    private boolean setText(String value, int pickerIndex)
        throws MultipleElementsFoundException,
            UiElementFetchingException {

        String currentValue = pickerHelper.getNumberPickerFieldValue(pickerIndex);
        if (!value.equals(currentValue)) {
            return pickerHelper.setTextInNumberPickerField(pickerIndex, value);
        }
        return true;
    }
}
