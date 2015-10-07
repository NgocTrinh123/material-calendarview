package com.prolificinteractive.materialcalendarview.utils;

public class MathUtils {

    public static int constrain(int val, int min, int max) {
        return Math.max(min, Math.min(max, val));
    }

    public static long constrain(long val, long min, long max) {
        return Math.max(min, Math.min(max, val));
    }
}
