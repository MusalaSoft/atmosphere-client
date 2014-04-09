package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.gesture.Anchor;
import com.musala.atmosphere.commons.gesture.Gesture;
import com.musala.atmosphere.commons.gesture.Timeline;

/**
 * Serves to create {@link Gesture} later performed by the gesture player.
 * 
 * @author delyan.dimitrov
 * 
 */
public class GestureCreator {
    private static int DOUBLE_TAP_INTERVAL = 150;

    /**
     * Creates a double tap {@link Gesture} on the passed point.
     * 
     * @param x
     *        - the x coordinate of the tap point
     * @param y
     *        - the y coordinate of the tap point
     * @return a {@link Gesture} that is a double tap
     */
    public static Gesture createDoubleTap(float x, float y) {
        Gesture doubleTap = new Gesture();

        Timeline firstTapTimeline = new Timeline();
        Anchor firstTap = new Anchor(x, y, 0);
        firstTapTimeline.add(firstTap);
        doubleTap.add(firstTapTimeline);

        Timeline secondTapTimeline = new Timeline();
        Anchor secondTap = new Anchor(x, y, DOUBLE_TAP_INTERVAL);
        secondTapTimeline.add(secondTap);
        doubleTap.add(secondTapTimeline);

        return doubleTap;
    }
}
