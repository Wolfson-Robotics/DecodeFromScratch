package org.firstinspires.ftc.teamcode.debug.util;

public enum CompassDirection {
    N(0),
    NE(45),
    E(90),
    SE(135),
    S(180),
    SW(225),
    W(270),
    NW(315);

    private final int degrees;

    CompassDirection(int degrees) {
        this.degrees = degrees;
    }

    /**
     * Returns the angle in degrees corresponding to this direction.
     * 0 = North, 90 = East, etc.
     */
    public int toDegrees() {
        return degrees;
    }

    /**
     * Returns the opposite direction (180 degrees away).
     */
    public CompassDirection opposite() {
        return fromDegrees((degrees + 180) % 360);
    }

    /**
     * Returns the direction rotated clockwise by 45°.
     */
    public CompassDirection clockwise() {
        return fromDegrees((degrees + 45) % 360);
    }

    /**
     * Returns the direction rotated counterclockwise by 45°.
     */
    public CompassDirection ccw() {
        return fromDegrees((degrees + 315) % 360);
    }

    public boolean cardinal(){
        return this == N || this == E || this == S || this == W;
    }
    public boolean intercardinal() {
        return this == NE || this == SE || this == SW || this == NW;
    }

    /**
     * Returns a unit vector [x, y] representation.
     * X = East, Y = North (like a standard Cartesian plane).
     */
    public double[] toUnitVector() {
        double radians = Math.toRadians(degrees);
        return new double[] { Math.sin(radians), Math.cos(radians) };
    }

    /**
     * Looks up the closest matching direction by degrees.
     * Input is normalized to the nearest 45° increment.
     * Returns north if <code>deg</code> is 0.
     */
    public static CompassDirection fromDegrees(int deg) {
        int normalized = ((deg % 360) + 360) % 360; // keep 0–359
        int snapped = (int) Math.round(normalized / 45.0) * 45 % 360;
        for (CompassDirection dir : values()) {
            if (dir.degrees == snapped) {
                return dir;
            }
        }
        return CompassDirection.N;
    }

}