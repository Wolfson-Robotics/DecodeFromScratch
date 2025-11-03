package org.firstinspires.ftc.teamcode.debug.util.nullable;

import java.util.Objects;

public class Nullable<T> {

    protected T val = null;
    public T set(T val) {
        return (this.val = val);
    }
    public T unset() {
        T oldVal = val;
        this.val = null;
        return oldVal;
    }
    public boolean isSet() {
        return this.val != null;
    }
    public T get() {
        return this.val;
    }

    public boolean equals(Object o) {
        return Objects.equals(o, this.val);
    }

    protected <N extends Nullable<?>> boolean validate(N nullable) {
        return nullable != null && nullable.isSet();
    }

}
