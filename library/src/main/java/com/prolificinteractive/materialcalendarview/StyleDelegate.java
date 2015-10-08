package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
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

    private final Context mContext;
    private final int mDesiredMonthHeight;
    private final int mDesiredDayOfWeekHeight;
    private final int mDesiredDayHeight;
    private final int mDesiredCellWidth;
    private final int mDesiredDaySelectorRadius;

    private int monthTextAppearanceResId;
    private int dayOfWeekTextAppearanceResId;
    private int dayTextAppearanceResId;
    private int firstDayOfWeek;

    private final SimpleDateFormat mTitleFormatter;
    private final SimpleDateFormat mDayOfWeekFormatter;
    private final NumberFormat mDayFormatter;

    private final TextPaint mMonthPaint = new TextPaint();
    private final TextPaint mDayOfWeekPaint = new TextPaint();
    private final TextPaint mDayPaint = new TextPaint();
    private final Paint mDaySelectorPaint = new Paint();
    private final Paint mDayHighlightPaint = new Paint();

    private ColorStateList mDayTextColor;

    public StyleDelegate(Context context, AttributeSet attrs, int defStyleAttr) {
        this.mContext = context;

        final TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.CalendarView, defStyleAttr, 0);

        firstDayOfWeek = a.getInt(R.styleable.CalendarView_firstDayOfWeek,
                Calendar.getInstance().getFirstDayOfWeek());

        setMonthTextAppearance(a.getResourceId(
                R.styleable.CalendarView_monthTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_Month));
        setDayOfWeekTextAppearance(a.getResourceId(
                R.styleable.CalendarView_weekDayTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_DayOfWeek));
        setDayTextAppearance(a.getResourceId(
                R.styleable.CalendarView_dateTextAppearance,
                R.style.TextAppearance_MaterialCalendarView_Day));

        ColorStateList daySelectorColor = a.getColorStateList(R.styleable.CalendarView_daySelectorColor);
        setDaySelectorColor(daySelectorColor);

        ColorStateList dayHighlightColor = ViewUtils.getThemeColorControlHighlight(context);
        setDayHighlightColor(dayHighlightColor);

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

        initPaints(res);
    }

    /**
     * Sets up the text and style properties for painting.
     */
    private void initPaints(Resources res) {
        final int monthTextSize = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_month_text_size);
        final int dayOfWeekTextSize = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_day_of_week_text_size);
        final int dayTextSize = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_day_text_size);

        mMonthPaint.setAntiAlias(true);
        mMonthPaint.setTextSize(monthTextSize);
        mMonthPaint.setTypeface(Typeface.DEFAULT);
        mMonthPaint.setTextAlign(Paint.Align.CENTER);
        mMonthPaint.setStyle(Paint.Style.FILL);

        mDayOfWeekPaint.setAntiAlias(true);
        mDayOfWeekPaint.setTextSize(dayOfWeekTextSize);
        mDayOfWeekPaint.setTypeface(Typeface.DEFAULT);
        mDayOfWeekPaint.setTextAlign(Paint.Align.CENTER);
        mDayOfWeekPaint.setStyle(Paint.Style.FILL);

        mDaySelectorPaint.setAntiAlias(true);
        mDaySelectorPaint.setStyle(Paint.Style.FILL);

        mDayHighlightPaint.setAntiAlias(true);
        mDayHighlightPaint.setStyle(Paint.Style.FILL);

        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextSize(dayTextSize);
        mDayPaint.setTypeface(Typeface.DEFAULT);
        mDayPaint.setTextAlign(Paint.Align.CENTER);
        mDayPaint.setStyle(Paint.Style.FILL);
    }

    public void setDaySelectorColor(ColorStateList dayBackgroundColor) {
        final int activatedColor = dayBackgroundColor.getColorForState(ViewUtils.STATE_ACTIVATED, 0);
        mDaySelectorPaint.setColor(activatedColor);
    }

    public void setDayHighlightColor(ColorStateList dayHighlightColor) {
        final int pressedColor = dayHighlightColor.getColorForState(ViewUtils.STATE_PRESSED, 0);
        mDayHighlightPaint.setColor(pressedColor);
    }

    public int getFirstDayOfWeek() {
        return firstDayOfWeek;
    }

    public void setMonthTextAppearance(int resId) {
        this.monthTextAppearanceResId = resId;
        ViewUtils.applyTextAppearance(mContext, mMonthPaint, resId);
    }

    public int getDayTextAppearance() {
        return dayTextAppearanceResId;
    }

    public void setDayTextAppearance(int resId) {
        this.dayTextAppearanceResId = resId;
        final ColorStateList textColor = ViewUtils.applyTextAppearance(mContext, mDayPaint, resId);
        if (textColor != null) {
            mDayTextColor = textColor;
        }
    }

    public int getDayOfWeekTextAppearance() {
        return dayOfWeekTextAppearanceResId;
    }

    public void setDayOfWeekTextAppearance(int resId) {
        this.dayOfWeekTextAppearanceResId = resId;
        ViewUtils.applyTextAppearance(mContext, mDayOfWeekPaint, resId);
    }

    public int getArrowButtonColor() {
        return Color.BLACK;
    }

    public int getMonthTextAppearance() {
        return monthTextAppearanceResId;
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

    public void drawMonth(SimpleMonthView monthView, Canvas canvas) {
        final float x = monthView.getPaddedWidth() / 2f;

        // Vertically centered within the month header height.
        final float lineHeight = mMonthPaint.ascent() + mMonthPaint.descent();
        final float y = (monthView.getMonthHeight() - lineHeight) / 2f;

        drawMonthLabel(canvas, mMonthPaint, monthView.getTitle(), x, y);
    }

    private final Calendar mDayOfWeekLabelCalendar = Calendar.getInstance();

    private CharSequence getDayOfWeekLabel(int dayOfWeek) {
        mDayOfWeekLabelCalendar.set(Calendar.DAY_OF_WEEK, dayOfWeek);
        return getWeekDayLabel(mDayOfWeekLabelCalendar.getTime());
    }

    public void drawDaysOfWeek(SimpleMonthView monthView, Canvas canvas) {
        final TextPaint p = mDayOfWeekPaint;
        final int headerHeight = monthView.getMonthHeight();
        final int rowHeight = monthView.getDayOfWeekHeight();
        final int colWidth = monthView.getCellWidth();

        // Text is vertically centered within the day of week height.
        final float halfLineHeight = (p.ascent() + p.descent()) / 2f;
        final int rowCenter = headerHeight + rowHeight / 2;

        for (int col = 0; col < SimpleMonthView.DAYS_IN_WEEK; col++) {
            final int colCenter = colWidth * col + colWidth / 2;
            final int colCenterRtl;
            if (ViewUtils.isLayoutRtl(monthView)) {
                colCenterRtl = monthView.getPaddedWidth() - colCenter;
            } else {
                colCenterRtl = colCenter;
            }

            final int dayOfWeek = (col + getFirstDayOfWeek()) % SimpleMonthView.DAYS_IN_WEEK;
            final CharSequence label = getDayOfWeekLabel(dayOfWeek);
            drawDayOfWeekLabel(canvas, p, label, colCenterRtl, rowCenter - halfLineHeight);
        }
    }

    public void drawDays(SimpleMonthView monthView, Canvas canvas) {
        final TextPaint p = mDayPaint;
        final int headerHeight = monthView.getMonthHeight() + monthView.getDayOfWeekHeight();
        final int rowHeight = monthView.getDayHeight();
        final int colWidth = monthView.getCellWidth();

        // Text is vertically centered within the row height.
        final float halfLineHeight = (p.ascent() + p.descent()) / 2f;
        int rowCenter = headerHeight + rowHeight / 2;

        for (int day = 1, col = monthView.findDayOffset(); day <= monthView.getDaysInMonth(); day++) {
            final int colCenter = colWidth * col + colWidth / 2;
            final int colCenterRtl;
            if (ViewUtils.isLayoutRtl(monthView)) {
                colCenterRtl = monthView.getPaddedWidth() - colCenter;
            } else {
                colCenterRtl = colCenter;
            }

            int[] stateMask;

            final boolean isDayEnabled = monthView.isDayEnabled(day);
            final float mDaySelectorRadius = monthView.getDaySelectorRadius();

            if (monthView.isDayActivated(day)) {
                stateMask = ViewUtils.STATE_ACTIVATED;

                // Adjust the circle to be centered on the row.
                drawDaySelected(canvas, mDaySelectorPaint,
                        colCenterRtl, rowCenter,
                        mDaySelectorRadius);

            } else if (monthView.isTouchedItem(day)) {
                stateMask = ViewUtils.STATE_PRESSED;

                if (isDayEnabled) {
                    // Adjust the circle to be centered on the row.
                    drawDayPressed(canvas, mDayHighlightPaint,
                            colCenterRtl, rowCenter,
                            mDaySelectorRadius);
                }
            } else {
                stateMask = isDayEnabled ? ViewUtils.STATE_ENALBED : ViewUtils.STATE_DISALBED;
            }

            p.setColor(mDayTextColor.getColorForState(stateMask, 0));

            drawDayLabel(canvas, p, getDayLabel(day), colCenterRtl, rowCenter - halfLineHeight);

            col++;

            if (col == SimpleMonthView.DAYS_IN_WEEK) {
                col = 0;
                rowCenter += rowHeight;
            }
        }
    }
}
