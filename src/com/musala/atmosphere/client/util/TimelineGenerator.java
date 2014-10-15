package com.musala.atmosphere.client.util;

import com.musala.atmosphere.client.geometry.Point;
import com.musala.atmosphere.commons.gesture.Anchor;
import com.musala.atmosphere.commons.gesture.Timeline;

/**
 * Houses static methods that generate commonly needed shapes represented by {@link Timeline} instances.
 * 
 * @author georgi.gaydarov
 * 
 */
public class TimelineGenerator {
    private static final float ARC_MAX_ANGLE = 355;

    private static final float ARC_MIN_ANGLE = 2;

    /**
     * Constructs a {@link Gesture} that, when drawn, results in a circular trajectory. Simplified version of
     * {@link #createCircle(Point, float, int, int, int)} that should be sufficient in most cases.
     * 
     * @param center
     *        - the coordinates of the circle origin (as a {@link Point})
     * @param radius
     *        - radius of the circle
     * @return the resulting populated {@link Timeline} instance
     */
    public static Timeline createCircle(Point center, float radius) {
        final int DEFAULT_DURATION = 1000;
        final int DEFAULT_STEPS = 20;
        final int DEFAULT_START = 0;
        return createCircle(center, radius, DEFAULT_START, DEFAULT_DURATION, DEFAULT_STEPS);
    }

    /**
     * Constructs a {@link Timeline} that, when drawn, results in a circular trajectory.
     * 
     * @param center
     *        - the coordinates of the circle origin (as a {@link Point})
     * @param radius
     *        - radius of the circle
     * @param startTime
     *        - the moment in time at which the first {@link Anchor} should be reached
     * @param totalDuration
     *        - the total duration of the resulting generated gesture (in milliseconds)
     * @param inSteps
     *        - amount of {@link Anchor} instances that the generated {@link Timeline} should contain
     * @return the resulting populated {@link Timeline} instance
     */
    public static Timeline createCircle(Point center, float radius, int startTime, int totalDuration, int inSteps) {
        final float FULL_RADIANS = (float) (Math.PI * 2.0f);
        Timeline result = new Timeline();
        final float STEP_ANGLE_RAD = FULL_RADIANS / inSteps;
        final float INSURANCE_ANGLE = 0.2f;

        for (float rotRad = 0; rotRad < INSURANCE_ANGLE + FULL_RADIANS; rotRad += STEP_ANGLE_RAD) {
            int x = (int) (center.getX() + Math.cos(rotRad) * radius);
            int y = (int) (center.getY() + Math.sin(rotRad) * radius);

            int timeProgress = (int) (totalDuration * (rotRad / FULL_RADIANS));
            Anchor point = new Anchor(x, y, startTime + timeProgress);
            result.add(point);
        }

        return result;
    }

    /**
     * Generates a curve (arc shape) between two points.
     * 
     * @param start
     *        - the starting point
     * @param end
     *        - the final point
     * @param angularDeg
     *        - the angle in degrees of the generated arc (in the range [{@value #ARC_MIN_ANGLE};
     *        {@value #ARC_MAX_ANGLE}]). For example, 180 would create half a circle
     * @param isPositive
     *        - indicates the direction of the requested arc - if it's set to <code>true</code> and the abcissa of the
     *        starting point is greater than that of the end point, then the arc will be <b>OVER</b> the lower point of
     *        the two (and under if <code>false</code>)
     * @return the resulting populated {@link Timeline} instance
     */
    public static Timeline createCurve(Anchor start, Anchor end, float angularDeg, boolean isPositive) {
        final int RECURSION_BOTTOM = 5;
        if (angularDeg < ARC_MIN_ANGLE || angularDeg > ARC_MAX_ANGLE) {
            String message = String.format("Angular value '%d' is not in the acceptable range [%d; %d].",
                                           angularDeg,
                                           ARC_MIN_ANGLE,
                                           ARC_MAX_ANGLE);
            throw new IllegalArgumentException(message);
        }
        return createCurve(start, end, angularDeg, isPositive, 0, RECURSION_BOTTOM);
    }

    private static Timeline createCurve(Anchor start,
                                        Anchor end,
                                        float angularDeg,
                                        boolean positive,
                                        int recursionLevel,
                                        int recursionMaximumLevel) {
        Timeline result = new Timeline();

        if (recursionLevel >= recursionMaximumLevel) {
            result.add(start);
            result.add(end);
            return result;
        }

        Anchor middle = getArcMiddle(start, end, angularDeg, positive);

        result.addAll(createCurve(start, middle, angularDeg / 2.0f, positive, recursionLevel + 1, recursionMaximumLevel));
        result.addAll(createCurve(middle, end, angularDeg / 2.0f, positive, recursionLevel + 1, recursionMaximumLevel));

        return result;
    }

    private static Anchor getArcMiddle(Anchor start, Anchor end, float angularDeg, boolean positive) {
        // Let start be A and end be B
        double x1 = start.getX();
        double y1 = start.getY();
        double x2 = end.getX();
        double y2 = end.getY();

        double dx = x2 - x1;
        double dy = y2 - y1;

        // distance between A and B
        double distance = Math.sqrt(dx * dx + dy * dy);

        // sine theorem will give us the distance from the middle of the line between A and B
        // and the center of the arc
        double beta = Math.toRadians(180.0 - angularDeg / 2.0);
        double betaShoulder = (Math.PI - beta) / 2.0;
        double height = Math.sin(betaShoulder) * distance / (Math.sin(beta / 2.0f) * 2.0);

        double middleX = x1 + dx / 2.0f;
        double middleY = y1 + dy / 2.0f;

        // finding normalized vector perpendicular to AB
        double vectorLength = Math.sqrt(dx * dx + dy * dy);
        double normX, normY;
        if (positive) {
            normX = -dy / vectorLength;
            normY = dx / vectorLength;
        } else {
            normX = dy / vectorLength;
            normY = -dx / vectorLength;
        }

        float arcMidX = (float) (middleX + normX * height);
        float arcMidY = (float) (middleY + normY * height);

        int moment = (end.getTimeAfterStart() + start.getTimeAfterStart()) / 2;
        return new Anchor(arcMidX, arcMidY, moment);
    }
}
