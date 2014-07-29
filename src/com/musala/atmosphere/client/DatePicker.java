package com.musala.atmosphere.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.log4j.Logger;

import com.musala.atmosphere.client.exceptions.ActionFailedException;
import com.musala.atmosphere.client.exceptions.InvalidCssQueryException;
import com.musala.atmosphere.client.exceptions.UiElementFetchingException;

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
            monthsStringToIntegerMap = new HashMap<String, Integer>();
            monthsIntegerToStringMap = new HashMap<Integer, String>();

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
         * @param monthNumber
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

    public static final String DATE_FORMAT = "dd-M-yyyy";

    private static final int MONTH_PICKER_INDEX = 0;

    private static final int DAY_PICKER_INDEX = 1;

    private static final int YEAR_PICKER_INDEX = 2;

    private static final String DATE_FORMATTER = "%s-%s-%s";

    private PickerHelper pickerHelper;

    public DatePicker(Screen screen) {
        super(screen);
        pickerHelper = new PickerHelper(screen);
    }

    @Override
    public boolean setValue(Calendar calendar)
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String year = String.valueOf(calendar.get(Calendar.YEAR));
        String day = String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        String month = Month.getStringRepresentation(calendar.get(Calendar.MONTH) + 1);
        boolean setTextResult = setText(year, YEAR_PICKER_INDEX);
        setTextResult &= setText(day, DAY_PICKER_INDEX);
        setTextResult &= setText(month, MONTH_PICKER_INDEX);
        return setTextResult;
    }

    /**
     * Gets the year in DatePicker.
     * 
     * @return - int instance of DatePicker's year field.
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws InvalidCssQueryException
     *         if the NumberPicker or EditText widgets are invalid.
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs.
     */
    public int getYear()
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String year = pickerHelper.getNumberPickerFieldValue(YEAR_PICKER_INDEX);
        int yearNumber = Integer.parseInt(year);
        return yearNumber;
    }

    /**
     * Gets the day in DatePicker.
     * 
     * @return - int instance of DatePicker's day field.
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws InvalidCssQueryException
     *         if the NumberPicker or EditText widgets are invalid.
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs.
     */
    public int getDay()
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String day = pickerHelper.getNumberPickerFieldValue(DAY_PICKER_INDEX);
        int dayNumber = Integer.parseInt(day);
        return dayNumber;
    }

    /**
     * Gets the month in DatePicker.
     * 
     * @return - int instance of DatePicker's month field.
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws InvalidCssQueryException
     *         if the NumberPicker or EditText widgets are invalid.
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs.
     */
    public int getMonth()
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String month = pickerHelper.getNumberPickerFieldValue(MONTH_PICKER_INDEX);
        int monthNumber = Month.getIntValue(month);
        return monthNumber;
    }

    @Override
    public Calendar getValue()
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String date;
        String month = pickerHelper.getNumberPickerFieldValue(MONTH_PICKER_INDEX);
        String day = pickerHelper.getNumberPickerFieldValue(DAY_PICKER_INDEX);
        String year = pickerHelper.getNumberPickerFieldValue(YEAR_PICKER_INDEX);

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
    public String getStringValue()
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String date;
        String month = pickerHelper.getNumberPickerFieldValue(MONTH_PICKER_INDEX);
        String day = pickerHelper.getNumberPickerFieldValue(DAY_PICKER_INDEX);
        String year = pickerHelper.getNumberPickerFieldValue(YEAR_PICKER_INDEX);

        date = String.format(DATE_FORMATTER, month, day, year);
        return date;

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
     * @throws XPathExpressionException
     *         if the conversion from CSS to XPath is unsuccessful for some reason.
     * @throws UiElementFetchingException
     *         if the NumberPicker or EditText elements are not present.
     * @throws InvalidCssQueryException
     *         if the NumberPicker or EditText widgets are invalid.
     * @throws ParserConfigurationException
     *         if an error with internal XPath configuration occurs.
     */
    private boolean setText(String value, int pickerIndex)
        throws XPathExpressionException,
            UiElementFetchingException,
            InvalidCssQueryException,
            ParserConfigurationException {

        String currentValue = pickerHelper.getNumberPickerFieldValue(pickerIndex);
        if (!value.equals(currentValue)) {
            return pickerHelper.setTextInNumberPickerField(pickerIndex, value);
        }
        return true;
    }
}