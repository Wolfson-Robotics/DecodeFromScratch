package org.firstinspires.ftc.teamcode.debug.util.robot;

import org.firstinspires.ftc.teamcode.debug.util.math.Vector2D;

public class RobotVector extends Vector2D {

    private final Vector2D lf, lb, rf, rb, front, back, robot;

    public RobotVector(Vector2D lf, Vector2D lb, Vector2D rf, Vector2D rb) {
        super(lf.add(rf).add(lb.add(rb)));
        this.lf = lf;
        this.lb = lb;
        this.rf = rf;
        this.rb = rb;
        this.front = lf.add(rf);
        this.back = lb.add(rb);
        this.robot = front.add(back);
    }

    //public RobotVector interpolate(RobotVector prev, )

    public Vector2D lf() {
        return lf;
    }
    public Vector2D lb() {
        return lb;
    }
    public Vector2D rf() {
        return rf;
    }
    public Vector2D rb() {
        return rb;
    }

    public Vector2D front() {
        return front;
    }
    public Vector2D back() {
        return back;
    }
    public Vector2D get() {
        return robot;
    }

    public static RobotVector empty() {
        return new RobotVector(new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(0, 0), new Vector2D(0, 0));
    }

}
