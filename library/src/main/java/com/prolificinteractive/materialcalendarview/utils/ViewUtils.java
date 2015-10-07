package com.prolificinteractive.materialcalendarview.utils;

import android.support.v4.view.ViewCompat;
import android.view.View;

public class ViewUtils {
    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }
}
