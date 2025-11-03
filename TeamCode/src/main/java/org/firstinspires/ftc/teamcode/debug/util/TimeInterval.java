package org.firstinspires.ftc.teamcode.debug.util;

import androidx.annotation.NonNull;

import java.util.concurrent.TimeUnit;

public final class TimeInterval implements Comparable<TimeInterval> {
    private final long seconds;
    private final int nanos;

    private TimeInterval(long seconds, int nanos) {
        this.seconds = seconds;
        this.nanos = nanos;
    }

    public static TimeInterval ofNanos(long nanosTotal) {
        long secs = nanosTotal / 1_000_000_000L;
        int nos = (int) (nanosTotal % 1_000_000_000L);
        if (nos < 0) {
            nos += 1_000_000_000;
            secs--;
        }
        return new TimeInterval(secs, nos);
    }
    public static TimeInterval of(long amount, TimeUnit unit) {
        long nanos = unit.toNanos(amount);
        return ofNanos(nanos);
    }

    public long toNanos() {
        return Math.addExact(Math.multiplyExact(seconds, 1_000_000_000L), nanos);
    }
    public long toMillis() {
        return seconds * 1000 + nanos / 1_000_000;
    }

    public long to(TimeUnit unit) {
        return unit.convert(toNanos(), TimeUnit.NANOSECONDS);
    }

    @Override
    public int compareTo(TimeInterval other) {
        int cmp = Long.compare(this.seconds, other.seconds);
        if (cmp != 0) return cmp;
        return Integer.compare(this.nanos, other.nanos);
    }

    public TimeInterval plus(TimeInterval other) {
        long sec = Math.addExact(this.seconds, other.seconds);
        int nano = this.nanos + other.nanos;
        return normalize(sec, nano);
    }
    public TimeInterval minus(TimeInterval other) {
        long sec = Math.subtractExact(this.seconds, other.seconds);
        int nano = this.nanos - other.nanos;
        return normalize(sec, nano);
    }

    public TimeInterval multipliedBy(long factor) {
        long totalNanos = Math.multiplyExact(this.toNanos(), factor);
        return ofNanos(totalNanos);
    }
    public TimeInterval dividedBy(long divisor) {
        long totalNanos = this.toNanos() / divisor;
        return ofNanos(totalNanos);
    }

    public TimeInterval plus(long amount, TimeUnit unit) {
        long nanos = unit.toNanos(amount);
        return this.plus(ofNanos(nanos));
    }
    public TimeInterval minus(long amount, TimeUnit unit) {
        long nanos = unit.toNanos(amount);
        return this.minus(ofNanos(nanos));
    }

    public TimeInterval multipliedBy(long amount, TimeUnit unit) {
        long factor = unit.toNanos(amount);
        long totalNanos = Math.multiplyExact(this.toNanos(), factor);
        return ofNanos(totalNanos);
    }
    public TimeInterval dividedBy(long amount, TimeUnit unit) {
        long divisor = unit.toNanos(amount);
        if (divisor == 0) throw new ArithmeticException("divide by zero");
        long totalNanos = this.toNanos() / divisor;
        return ofNanos(totalNanos);
    }

    private static TimeInterval normalize(long sec, int nano) {
        long extraSec = nano / 1_000_000_000;
        int adjustedNano = nano % 1_000_000_000;
        if (adjustedNano < 0) {
            adjustedNano += 1_000_000_000;
            extraSec--;
        }
        sec = Math.addExact(sec, extraSec);
        return new TimeInterval(sec, adjustedNano);
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof TimeInterval)) return false;
        TimeInterval other = (TimeInterval) o;
        return this.seconds == other.seconds && this.nanos == other.nanos;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(seconds) ^ Integer.hashCode(nanos);
    }

    @NonNull
    @Override
    public String toString() {
        return "TimeInterval[" + seconds + "s," + nanos + "ns]";
    }
}
