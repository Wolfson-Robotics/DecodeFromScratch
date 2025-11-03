package org.firstinspires.ftc.teamcode.debug.util.nullable;

import java.util.Objects;

public class NullableDouble extends Nullable<Double> {

    public boolean equals(double other) {
        return isSet() && this.val == other;
    }
    public boolean equals(int other) {
        return isSet() && this.val == other;
    }
    public boolean equals(NullableDouble other) {
        return isSet() && other.isSet() && Objects.equals(this.val, other.val);
    }
    public boolean equals(NullableInt other) {
        return isSet() && other.isSet() && Objects.equals(this.val, other.val);
    }
    public boolean greater(double other) {
        return isSet() && this.val > other;
    }
    public boolean greater(int other) {
        return isSet() && this.val > other;
    }
    public boolean greater(NullableDouble other) {
        return validate(other) && greater(other.val);
    }
    public boolean greater(NullableInt other) {
        return validate(other) && greater(other.val);
    }
    public boolean greaterOrEqual(double other) {
        return isSet() && this.val >= other;
    }
    public boolean greaterOrEqual(int other) {
        return isSet() && this.val >= other;
    }
    public boolean greaterOrEqual(NullableDouble other) {
        return validate(other) && greaterOrEqual(other.val);
    }
    public boolean greaterOrEqual(NullableInt other) {
        return validate(other) && greaterOrEqual(other.val);
    }

}
