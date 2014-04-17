package com.musala.atmosphere.client.uiutils;

import com.musala.atmosphere.commons.beans.SwipeDirection;
import com.musala.atmosphere.commons.gesture.Anchor;
import com.musala.atmosphere.commons.gesture.Gesture;
import com.musala.atmosphere.commons.gesture.Timeline;
import com.musala.atmosphere.commons.util.Pair;

/**
 * Serves to create {@link Gesture} later performed by the gesture player.
 * 
 * @author delyan.dimitrov
 * 
 */

public class GestureCreator {
    private static int DOUBLE_TAP_INTERVAL = 150;

    private static int SWIPE_INTERVAL = 250;

    private static int SWIPE_DISTANCE = 75;

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

    /**
     * Creates a swipe gesture {@link Gesture} from passed point in passed direction.
     * 
     * @param x
     *        - the x coordinate of the start point
     * @param y
     *        - the y coordinate of the start point
     * @param swipeDirection
     *        - the direction of the swipe
     * @param resolution
     *        - this is a pair which present the resolution of the screen
     * @return a {@link Gesture} that is a swipe
     */

    public static Gesture createSwipe(float x, float y, SwipeDirection swipeDirection, Pair<Integer, Integer> resolution) {
        Gesture swipe = new Gesture();
        Anchor startSwipe = new Anchor(x, y, 0);
        Timeline swipeTimeline = new Timeline();
        swipeTimeline.add(startSwipe);

        float endX = x;
        float endY = y;

        switch (swipeDirection) {
            case UP:
                endY = Math.max(y - SWIPE_DISTANCE, 0);
                break;
            case DOWN:
                endY = Math.min(y + SWIPE_DISTANCE, resolution.getValue());
                break;
            case LEFT:
                endX = Math.max(x - SWIPE_DISTANCE, 0);
                break;
            case RIGHT:
                endX = Math.min(x + SWIPE_DISTANCE, resolution.getKey());
                break;
            default:
                endX = x;
                endY = y;
                break;
        }
        Anchor endSwipe = new Anchor(endX, endY, SWIPE_INTERVAL);
        swipeTimeline.add(endSwipe);
        swipe.add(swipeTimeline);
        return swipe;
    }
}
