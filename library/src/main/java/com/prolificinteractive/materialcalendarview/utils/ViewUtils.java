package com.prolificinteractive.materialcalendarview.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;

public class ViewUtils {

    public static boolean isLayoutRtl(View view) {
        return ViewCompat.getLayoutDirection(view) == ViewCompat.LAYOUT_DIRECTION_RTL;
    }

    public static int getThemeSelectableBackgroundId(Context context) {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        int colorAttr = context.getResources().getIdentifier(
                "selectableItemBackgroundBorderless", "attr", context.getPackageName());

        if (colorAttr == 0) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                colorAttr = android.R.attr.selectableItemBackgroundBorderless;
            } else {
                colorAttr = android.R.attr.selectableItemBackground;
            }
        }

        TypedValue outValue = new TypedValue();
        context.getTheme().resolveAttribute(colorAttr, outValue, true);
        return outValue.resourceId;
    }

    public static ColorStateList getThemeColorControlHighlight(Context context) {
        //Get selectableItemBackgroundBorderless defined for AppCompat
        int colorAttr;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            colorAttr = android.R.attr.colorControlHighlight;
        } else {
            colorAttr = context.getResources().getIdentifier(
                    "selectableItemBackgroundBorderless", "attr", context.getPackageName());
        }

        if (colorAttr == 0) {
            return null;
        }

        final TypedArray ta = context.obtainStyledAttributes(new int[]{colorAttr});
        ColorStateList color = ta.getColorStateList(0);
        ta.recycle();

        return color;
    }
}
