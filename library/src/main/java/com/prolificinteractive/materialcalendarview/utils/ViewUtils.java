package com.prolificinteractive.materialcalendarview.utils;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Paint;
import android.os.Build;
import android.support.v4.view.ViewCompat;
import android.util.TypedValue;
import android.view.View;

public class ViewUtils {

    public static final int[] STATE_ACTIVATED = {android.R.attr.state_enabled, android.R.attr.state_activated};
    public static final int[] STATE_PRESSED = {android.R.attr.state_enabled, android.R.attr.state_pressed};
    public static final int[] STATE_ENALBED = {android.R.attr.state_enabled};
    public static final int[] STATE_DISALBED = {};

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

    /**
     * Applies the specified text appearance resource to a paint, returning the
     * text color if one is set in the text appearance.
     *
     * @param p     the paint to modify
     * @param resId the resource ID of the text appearance
     * @return the text color, if available
     */
    public static ColorStateList applyTextAppearance(Context context, Paint p, int resId) {
        int[] attrs = {android.R.attr.textSize, android.R.attr.textColor};
        final TypedArray ta = context.obtainStyledAttributes(null, attrs, 0, resId);

// TODO fontFamily
//        final String fontFamily = ta.getString(R.styleable.TextAppearance_fontFamily);
//        if (fontFamily != null) {
//            p.setTypeface(Typeface.create(fontFamily, 0));
//        }

        p.setTextSize(ta.getDimensionPixelSize(0, (int) p.getTextSize()));

        final ColorStateList textColor = ta.getColorStateList(1);
        if (textColor != null) {
            final int enabledColor = textColor.getColorForState(STATE_ENALBED, 0);
            p.setColor(enabledColor);
        }

        ta.recycle();

        return textColor;
    }
}
