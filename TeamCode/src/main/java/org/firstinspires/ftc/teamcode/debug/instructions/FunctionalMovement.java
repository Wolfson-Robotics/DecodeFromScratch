package org.firstinspires.ftc.teamcode.debug.instructions;

import org.firstinspires.ftc.teamcode.debug.DebugAuto;
import org.firstinspires.ftc.teamcode.debug.util.TimeInterval;
import org.firstinspires.ftc.teamcode.debug.util.math.function.Cubic;

public class FunctionalMovement implements DebugInstruction {

    private final Cubic lf, lb, rf, rb;
    private final TimeInterval duration;

    public FunctionalMovement(Cubic lf, Cubic lb, Cubic rf, Cubic rb, TimeInterval duration) {
        this.lf = lf;
        this.lb = lb;
        this.rf = rf;
        this.rb = rb;
//        this.cmp = cmp;
        this.duration = duration;
    }

    // TODO: Implement cubic position function and position checking if necessary
    @Override
    public void run(DebugAuto instance) {
        long start = System.nanoTime();
        while (TimeInterval.ofNanos(System.nanoTime() - start).compareTo(duration) < 0) {
            instance.lf_drive.setPower(lf.evaluate(System.nanoTime() - start));
            instance.lb_drive.setPower(lb.evaluate(System.nanoTime() - start));
            instance.rf_drive.setPower(rf.evaluate(System.nanoTime() - start));
            instance.rb_drive.setPower(rb.evaluate(System.nanoTime() - start));
            /*
            instance.moveBot(
                    lf.evaluate(instance.getElapsedTime()),
                    lb.evaluate(instance.getElapsedTime()),
                    rf.evaluate(instance.getElapsedTime()),
                    rb.evaluate(instance.getElapsedTime()),
                    instance.lf_drive,
                    cmp.evaluate(instance.getElapsedTime())
            );*/
        }
    }
}
