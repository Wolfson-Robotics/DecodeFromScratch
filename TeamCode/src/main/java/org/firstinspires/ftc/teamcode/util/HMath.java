package org.firstinspires.ftc.teamcode.util;

//Handy Math functions (HMath to avoid name alike java.lang.math)
public class HMath {

    //linear interpolation
    public static double lerp(double start, double end, double current) {
        return start + current * (end - start);
    }

}
