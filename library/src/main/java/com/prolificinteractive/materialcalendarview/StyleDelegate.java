package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.util.AttributeSet;

import com.prolificinteractive.materialcalendarview.utils.ViewUtils;

import java.util.Calendar;

public class StyleDelegate {

    private int monthTextAppearanceResId;
    private int dayOfWeekTextAppearanceResId;
    private int dayTextAppearanceResId;
    private ColorStateList daySelectorColor;
    private int firstDayOfWeek;
    private ColorStateList dayHighlightColor;

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
}
