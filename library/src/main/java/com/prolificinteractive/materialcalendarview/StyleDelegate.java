package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.util.AttributeSet;

import com.prolificinteractive.materialcalendarview.utils.ViewUtils;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class StyleDelegate {

    private static final String DEFAULT_TITLE_FORMAT = "MMMM y";
    private static final String DAY_OF_WEEK_FORMAT = "E";

    private final int mDesiredMonthHeight;
    private final int mDesiredDayOfWeekHeight;
    private final int mDesiredDayHeight;
    private final int mDesiredCellWidth;
    private final int mDesiredDaySelectorRadius;

    private int monthTextAppearanceResId;
    private int dayOfWeekTextAppearanceResId;
    private int dayTextAppearanceResId;
    private ColorStateList daySelectorColor;
    private int firstDayOfWeek;
    private ColorStateList dayHighlightColor;

    private final SimpleDateFormat mTitleFormatter;
    private final SimpleDateFormat mDayOfWeekFormatter;
    private final NumberFormat mDayFormatter;

    //TODO
    private ColorStateList monthTextColor = null;
    private ColorStateList dayOfWeekTextColor = null;
    private ColorStateList dayTextColor = null;

    public StyleDelegate(Context context, AttributeSet attrs, int defStyleAttr) {
        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CalendarView, defStyleAttr, 0);

        firstDayOfWeek = a.getInt(R.styleable.CalendarView_firstDayOfWeek,
                Calendar.getInstance().getFirstDayOfWeek());

        monthTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_monthTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_Month);
        dayOfWeekTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_weekDayTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_DayOfWeek);
        dayTextAppearanceResId = a.getResourceId(
                R.styleable.CalendarView_dateTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_Day);

        daySelectorColor = a.getColorStateList(R.styleable.CalendarView_daySelectorColor);

        dayHighlightColor = ViewUtils.getThemeColorControlHighlight(context);

        a.recycle();

        final Resources res = context.getResources();
        mDesiredMonthHeight = res.getDimensionPixelSize(R.dimen.mcv_date_picker_month_height);
        mDesiredDayOfWeekHeight = res.getDimensionPixelSize(R.dimen.mcv_date_picker_day_of_week_height);
        mDesiredDayHeight = res.getDimensionPixelSize(R.dimen.mcv_date_picker_day_height);
        mDesiredCellWidth = res.getDimensionPixelSize(R.dimen.mcv_date_picker_day_width);
        mDesiredDaySelectorRadius = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_day_selector_radius);

        final Locale locale = res.getConfiguration().locale;
        final String titleFormat;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR2) {
            titleFormat = DateFormat.getBestDateTimePattern(locale, DEFAULT_TITLE_FORMAT);
        } else {
            titleFormat = DEFAULT_TITLE_FORMAT;
        }
        mTitleFormatter = new SimpleDateFormat(titleFormat, locale);
        mDayOfWeekFormatter = new SimpleDateFormat(DAY_OF_WEEK_FORMAT, locale);
        mDayFormatter = NumberFormat.getIntegerInstance(locale);
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public int getDayTextAppearance() {
        return dayTextAppearanceResId;
    }

    public void setDayTextAppearance(int dayTextAppearance) {
        this.dayTextAppearanceResId = dayTextAppearance;
    }

    public int getDayOfWeekTextAppearance() {
        return dayOfWeekTextAppearanceResId;
    }

    public void setDayOfWeekTextAppearance(int dayOfWeekTextAppearance) {
        this.dayOfWeekTextAppearanceResId = dayOfWeekTextAppearance;
    }

    public int getArrowButtonColor() {
        return Color.BLACK;
    }

    public int getMonthTextAppearance() {
        return monthTextAppearanceResId;
    }

    public ColorStateList getSelectionColor() {
        return daySelectorColor;
    }

    public ColorStateList getHighlightColor() {
        return dayHighlightColor;
    }

    public ColorStateList getMonthTextColor() {
        return monthTextColor;
    }

    public ColorStateList getDayOfWeekTextColor() {
        return dayOfWeekTextColor;
    }

    public ColorStateList getDayTextColor() {
        return dayTextColor;
    }

    public void setFirstDayOfWeek(int firstDayOfWeek) {
        this.firstDayOfWeek = firstDayOfWeek;
    }

    public void drawMonthLabel(Canvas canvas, TextPaint paint, CharSequence label, float x, float y) {
        canvas.drawText(label.toString(), x, y, paint);
    }

    public void drawDayOfWeekLabel(Canvas canvas, TextPaint paint, CharSequence label, float x, float y) {
        canvas.drawText(label.toString(), x, y, paint);
    }

    public void drawDayLabel(Canvas canvas, TextPaint paint, CharSequence label, float x, float y) {
        canvas.drawText(label.toString(), x, y, paint);
    }

    public void drawDayPressed(Canvas canvas, Paint paint, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public void drawDaySelected(Canvas canvas, Paint paint, float x, float y, float radius) {
        canvas.drawCircle(x, y, radius, paint);
    }

    public int getDesiredMonthHeight() {
        return mDesiredMonthHeight;
    }

    public int getDesiredDayOfWeekHeight() {
        return mDesiredDayOfWeekHeight;
    }

    public int getDesiredDayHeight() {
        return mDesiredDayHeight;
    }

    public int getDesiredDaySelectorRadius() {
        return mDesiredDaySelectorRadius;
    }

    public int getDesiredCellWidth() {
        return mDesiredCellWidth;
    }

    public CharSequence getMonthLabel(Date time) {
        return mTitleFormatter.format(time);
    }

    public CharSequence getWeekDayLabel(Date time) {
        return mDayOfWeekFormatter.format(time);
    }

    public CharSequence getDayLabel(int id) {
        return mDayFormatter.format(id);
    }
}
