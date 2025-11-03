package org.firstinspires.ftc.teamcode.debug.handlers;

import static com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.teamcode.debug.handlers.HardwareComponentHandler;
import org.firstinspires.ftc.teamcode.debug.util.GeneralUtils;
import org.firstinspires.ftc.teamcode.debug.util.IntegerBounds;
import org.firstinspires.ftc.teamcode.debug.util.nullable.NullableDouble;

import java.util.HashMap;
import java.util.Map;

public class DcMotorExHandler extends HardwareComponentHandler<DcMotorEx> {

    private int lowerPos = Integer.MIN_VALUE;
    private int upperPos = Integer.MAX_VALUE;

    private Map<IntegerBounds, Double> speedMap = new HashMap<>();

    // Manual control runtime variables
    private boolean stasisAchieved = false, lowerLimited = false, upperLimited = false, setMode = false, startedMoving = false;
    private double lastPoweredPos;
    private final NullableDouble cachedPos = new NullableDouble();

    // todo: make this dynamic
    private int limitTolerance = 5;
    private boolean runWithEncoder;

    private DcMotor.RunMode deviceMode;


    private static HardwareMap hardwareMap;
    public static void setHardwareMap(HardwareMap hardwareMap) {
        DcMotorExHandler.hardwareMap = hardwareMap;
    }

    // The "runWithEncoder" variable does not *necessarily* (although generally) imply that
    // the motor does not physically have an encoder linked to it; it can simply indicate
    // that the caller wants the mechanisms that control the motor's position depending on
    // its encoder values to be disabled.
    public DcMotorExHandler(String deviceName, DcMotorEx device, boolean runWithEncoder) {
        super(device);
        this.runWithEncoder = runWithEncoder;
        if (this.runWithEncoder) {
            device.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            this.startPos = device.getCurrentPosition();
            this.lastPoweredPos = device.getCurrentPosition();
        }
        this.deviceMode = device.getMode();
        this.name = deviceName;
    }
    public DcMotorExHandler(String deviceName, DcMotorEx device) {
        this(deviceName, device, true);
    }
    public DcMotorExHandler(DcMotorEx device, boolean hasEncoder) {
        this("Unknown", device, hasEncoder);
    }
    public DcMotorExHandler(DcMotorEx device) {
        this(device, true);
    }
    public DcMotorExHandler(String deviceName, boolean runWithEncoder) {
        this(deviceName, hardwareMap.get(DcMotorEx.class, deviceName), runWithEncoder);
    }
    public DcMotorExHandler(String deviceName) {
        this(deviceName, true);
    }


    private synchronized void driveMotor(double targetPosition, double power) {
        if (equPos(targetPosition)) {
            this.device.setMode(this.deviceMode);
            return;
        }
        this.device.setTargetPosition((int) targetPosition);
        if (this.device.getMode() != DcMotor.RunMode.RUN_TO_POSITION) {
            this.deviceMode = this.device.getMode();
            this.setMode = false;
        }
        this.device.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        this.device.setPower(powerDir(targetPosition, power));
    }
    private synchronized void driveMotor(double targetPosition) {
        driveMotor(targetPosition, 1);
    }

    private int dirMult() {
        return getDirection() == Direction.REVERSE ? -1 : 1;
    }
    private int powerMult() {
        return GeneralUtils.signClamp(getPower());
    }
    private double powerDir(double targetPosition, double mult) {
        return -dirMult() * (targetPosition > getPosition() ? mult : -mult);
    }
    private double powerDir(double targetPosition) {
        return powerDir(targetPosition, 1);
    }
    private boolean towardsLower(double power) {
        return (dirMult()*power) < 0;
    }
    private boolean towardsUpper(double power) {
        return (dirMult()*power) > 0;
    }

    public synchronized boolean lLim() {
        if (getPosition() <= this.lowerPos) {
            driveMotor(this.lowerPos, 0.05);
            return true;
        }
        return false;
    }
    public synchronized boolean uLim() {
        if (getPosition() >= this.upperPos) {
            driveMotor(this.upperPos, 0.05);
            return true;
        }
        return false;
    }
    public synchronized boolean limit() {
        if (posWithin()) {
            return false;
        }
        if (getPosition() <= this.lowerPos) {
            driveMotor(this.lowerPos, 0.05);
        } else if (getPosition() >= this.upperPos) {
            driveMotor(this.upperPos, 0.05);
        }
        return true;
    }
    public synchronized boolean limit(double power) {
        if (posWithin()) {
            return false;
        }
        if (getPosition() < this.lowerPos) {
            driveMotor(this.lowerPos, power);
        } else if (getPosition() > this.upperPos) {
            driveMotor(this.upperPos, power);
        } else {
            return false;
        }
        return true;
    }

    public synchronized boolean outOfLowerLimit() {
        return getPosition() < this.lowerPos;
    }
    public synchronized boolean isAtLowerLimit() {
        return Math.abs(getPosition() - this.lowerPos) <= limitTolerance;
    }
    public synchronized boolean isOrOutOfLowerLimit() {
        return outOfLowerLimit() || isAtLowerLimit();
    }
    public synchronized boolean outOfUpperLimit() {
        return getPosition() > this.upperPos;
    }
    public synchronized boolean isAtUpperLimit() {
        return Math.abs(getPosition() - this.upperPos) <= limitTolerance;
    }
    public synchronized boolean isOrOutOfUpperLimit() {
        return outOfUpperLimit() || isAtUpperLimit();
    }
    public synchronized boolean outOfLimit() {
        return outOfLowerLimit() || outOfUpperLimit();
    }
    public synchronized boolean isAtLimit() {
        return isAtLowerLimit() || isAtUpperLimit();
    }
    public synchronized boolean isOrOutOfLimit() {
        return outOfLimit() || isAtLimit();
    }



    public synchronized void zeroPower() {
        if (!this.runWithEncoder || !this.startedMoving) {
            device.setPower(0);
            return;
        }
//        this.setMode = false;
        if (!this.stasisAchieved && !this.lowerLimited && !this.upperLimited) {
            driveMotor(this.lastPoweredPos, 0.05);
            this.stasisAchieved = true;
        }
    }
    public synchronized void setPower(double power) {
        if (power == 0) {
            zeroPower();
            return;
        }

        if (!this.setMode) {
            this.device.setMode(this.deviceMode);
            this.setMode = true;
        }

        cachePosition();
        if (this.startedMoving && this.runWithEncoder && towardsLower(power)) {
            if (this.lowerLimited && isAtLowerLimit()) return;
            if (isOrOutOfLowerLimit()) {
//                limit();
                this.lastPoweredPos = this.lowerPos;
                this.setMode = false;
                this.upperLimited = false;
                if (!this.lowerLimited) {
                    driveMotor(this.lowerPos, 0.05);
                    this.lowerLimited = true;
                }
                decachePosition();
                return;
            }
        }
        else if (this.startedMoving && this.runWithEncoder && towardsUpper(power)) {
            if (this.upperLimited && isAtUpperLimit()) return;
            if (isOrOutOfUpperLimit()) {
//                limit();
                this.lastPoweredPos = this.upperPos;
                this.setMode = false;
                this.lowerLimited = false;
                if (!this.upperLimited) {
                    driveMotor(this.upperPos, 0.05);
                    this.upperLimited = true;
                }
                decachePosition();
                return;
            }
        }

        this.startedMoving = true;
        this.lowerLimited = false;
        this.upperLimited = false;
        this.stasisAchieved = false;

        if (this.runWithEncoder) this.lastPoweredPos = getPosition();

//        if (!this.setMode) {
//            this.device.setMode(this.deviceMode);
//            this.setMode = true;
//        }
        if (!this.speedMap.isEmpty()) {
            this.speedMap.forEach((bounds, mult) -> {
                if (bounds.inBetweenClosed(getPosition())) this.device.setPower(power * mult);
            });
        } else {
            this.device.setPower(power);
        }
        decachePosition();

    }

    public synchronized void setPosition(double position) {
        this.setPosition(position, 1);
    }
    public synchronized void setPosition(double position, double power) {
        if (!posWithin(position)) return;
        this.device.setMode(this.deviceMode);
        driveMotor((int) position, power);
        while (getPosition() != position) {
            // Same method that idle() in LinearOpMode uses
            Thread.yield();
        }
    }

    public synchronized void resetEncoder() {
        this.device.setMode(DcMotorEx.RunMode.STOP_AND_RESET_ENCODER);
    }
    public synchronized void setDirection(Direction direction) {
        this.device.setDirection(direction);
    }
    // TODO: Possibly merge tripEncoder and disableEncoder due to potential similarity of intents
    // In case the encoder stops working, add a failsafe so that under the appropriate circumstances (such as during a match),
    // we may bypass the mechanisms controlling the motor's position which would stop functioning correctly.
    public synchronized void tripEncoder() {
        this.disableEncoder();
        this.lowerLimited = false;
        this.upperLimited = false;
        this.stasisAchieved = false;
        this.startedMoving = true;
    }
    public synchronized void disableEncoder() {
        this.runWithEncoder = false;
    }
    public synchronized void enableEncoder() {
        this.runWithEncoder = true;
    }
    public synchronized void enableBrake() {
        this.device.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }
    public synchronized void disableBrake() {
        this.device.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
    }


    public boolean posWithin(double position) {
        return this.lowerPos <= position && position <= this.upperPos;
    }
    private synchronized boolean posWithin() {
        return posWithin(getPosition());
    }

    public boolean equPos(double pos1, double pos2) {
        return Math.abs(pos1 - pos2) <= getTargetPositionTolerance();
    }
    public boolean equPos(double position) {
        return equPos(position, getPosition());
    }

    public boolean pastPos(double post, double curr) {
        return getDirection() == Direction.REVERSE ? curr < post : curr > post;
    }
    public boolean pastPos(double position) {
        return pastPos(position, getPosition());
    }
    public boolean pastRelativePos(double post, double currPos) {
        return pastPos(post, currPos);
    }
    public boolean pastRelativePos(double position) {
        return pastRelativePos(position, getRelativePosition());
    }

    public boolean behindPos(double post, double curr) {
        return getDirection() == Direction.REVERSE ? curr > post : curr < post;
    }
    public boolean behindPos(double position) {
        return behindPos(position, getPosition());
    }
    public boolean behindRelativePos(double post, double currPos) {
        return behindPos(post, currPos);
    }
    public boolean behindRelativePos(double position) {
        return behindRelativePos(position, getRelativePosition());
    }

    public boolean reachedPos(double post, double curr) {
        return (dirMult()*powerMult()) < 0 ? pastPos(post, curr) : behindPos(post, curr);
    }
    public boolean reachedPos(double position) {
        return reachedPos(position, getPosition());
    }
    public boolean reachedRelativePos(double post, double currPos) {
        return powerMult() < 0 ? pastRelativePos(post, currPos) : behindRelativePos(post, currPos);
    }
    public boolean reachedRelativePos(double position) {
        return reachedRelativePos(position, getRelativePosition());
    }

    public boolean forward(double power) {
        return dirMult()*power>0;
    }
    public boolean backward(double power) {
        return dirMult()*power<0;
    }

    public double diff(double pos1, double pos2) {
        double diff = dirMult()*(pos2 - pos1);
        return Math.abs(diff) < getTargetPositionTolerance() ? 0 : diff;
    }
    public double diff(double position) {
        return diff(getPosition(), position);
    }
    public double relativeDiff(double position) {
        return diff(getRelativePosition(), position);
    }


    public void setSpeedMap(Map<IntegerBounds, Double> speedMap) {
        this.speedMap = speedMap;
    }
    public void setPositionBounds(int lowerPos, int upperPos) {
        this.lowerPos = (int) (Math.min(lowerPos, upperPos) + this.startPos);
        this.upperPos = (int) (Math.max(lowerPos, upperPos) + this.startPos);
    }

    public synchronized double getPower() {
        return this.device.getPower();
    }
    public synchronized double getPosition() {
        return this.cachedPos.isSet() ? this.cachedPos.get() : (double) this.device.getCurrentPosition();
    }
    // Alias for getPosition()
    public synchronized int getCurrentPosition() {
        return (int) getPosition();
    }
    private synchronized double cachePosition() {
        return this.cachedPos.set(getPosition());
    }
    private synchronized double decachePosition() {
        return this.cachedPos.unset();
    }
    public int getTargetPositionTolerance() {
        return this.device.getTargetPositionTolerance();
    }
    public synchronized DcMotorEx.Direction getDirection() {
        return this.device.getDirection();
    }

}