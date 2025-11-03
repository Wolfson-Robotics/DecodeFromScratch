package org.firstinspires.ftc.teamcode.debug.util.math;

import android.graphics.PointF;

import org.firstinspires.ftc.teamcode.debug.util.CompassDirection;

public class Vector2D extends PointF {
    public Vector2D(Vector2D vec) {
        super(vec.x, vec.y);
    }
    public Vector2D(double x, double y) {
        super((float)x, (float)y);
    }

    public Vector2D add(Vector2D v) {
        return new Vector2D(this.x + v.x, this.y + v.y);
    }

    public double magnitude() {
        return Math.hypot(x, y);
    }

    public double dot(Vector2D v) {
        return this.x * v.x + this.y * v.y;
    }

    public double cosineSimilarity(Vector2D v) {
        double magA = magnitude();
        double magB = v.magnitude();
        return (magA == 0d || magB == 0d) ? 0d : dot(v) / (magA * magB);
    }

    public CompassDirection compassDirection() {
        // atan2 uses (y, x). 0 rad points east. Convert to degrees with 0=N, 90=E.
        double angle = Math.toDegrees(Math.atan2(x, y));
        // Normalize to 0–359
        int totalAngle = (int) Math.round((angle % 360 + 360) % 360);
        return CompassDirection.fromDegrees(totalAngle);
    }
}
