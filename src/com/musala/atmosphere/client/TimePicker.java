package com.musala.atmosphere.client;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.musala.atmosphere.client.exceptions.ActionFailedException;
import com.musala.atmosphere.commons.PickerAction;
import com.musala.atmosphere.commons.RoutingAction;

/**
 * Class for interactions with time picker widget.
 * 
 * @author filareta.yordanova
 * 
 */

public class TimePicker extends PickerView {
    private static final String TIME_FORMAT = "h:mm a";

    private static final String TIME_24_HOURS_FORMAT = "hh:mm";

    public TimePicker(DeviceCommunicator communicator) {
        super(communicator);
    }

    @Override
    public boolean setValue(Calendar value) {
        // TODO set time in time picker
        return false;
    }

    @Override
    public Calendar getValue() {
        String response = (String) communicator.sendAction(RoutingAction.TIME_PICKER_INTERACTION, PickerAction.GET_TIME);
        Date parsedTime = null;
        parsedTime = parseTime(response, TIME_FORMAT);

        if (parsedTime == null) {
            parsedTime = parseTime(response, TIME_24_HOURS_FORMAT);
        }

        if (parsedTime == null) {
            throw new ActionFailedException("Getting current time from time picker widget failed.");
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(parsedTime);

        return calendar;
    }

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
}
