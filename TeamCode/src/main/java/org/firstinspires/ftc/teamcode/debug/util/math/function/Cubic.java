package org.firstinspires.ftc.teamcode.debug.util.math.function;

public class Cubic {

    private double a, b, c, d;

    public Cubic(double a, double b, double c, double d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
    }

    public double evaluate(double x) {
        return a * Math.pow(x, 3) + b * Math.pow(x, 2) + c * x + d;
    }

}
