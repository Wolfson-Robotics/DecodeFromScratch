package org.firstinspires.ftc.teamcode.util;

public class Rotation {

    //Wrap the degree to 0 -> 90 -> 179.9 -> -179.9 -> -90 -> 0
    public static double wrap180(double deg) {
        deg = deg % 360;
        if (deg > 180) deg -= 360;
        if (deg < -180) deg += 360;
        return deg;
    }

    //Wrap the degree to 0 -> 360
    public static double wrap360(double deg) {
        deg = deg % 360;
        if (deg > 180) deg -= 360;
        if (deg < -180) deg += 360;
        return deg;
    }

    //Prevent overreach of max and min and make yaw closest to whichever value
    public static double limitAndRound(double deg, double min, double max) {
        if (deg > max || deg < min) {
            double distToMax = Math.abs(deg - max);
            double distToMin = Math.abs(deg - min);
            deg = (distToMax < distToMin) ? max : min;
        }
        return deg;
    }


}
