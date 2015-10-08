/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.prolificinteractive.materialcalendarview;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.accessibility.AccessibilityNodeInfoCompat;
import android.support.v4.widget.ExploreByTouchHelper;
import android.text.TextPaint;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.prolificinteractive.materialcalendarview.utils.MathUtils;
import com.prolificinteractive.materialcalendarview.utils.ViewUtils;

import java.util.Calendar;
import java.util.List;

/**
 * A calendar-like view displaying a specified month and the appropriate selectable day numbers
 * within the specified month.
 */
class SimpleMonthView extends View {

    protected static final int DAYS_IN_WEEK = 7;
    private static final int MAX_WEEKS_IN_MONTH = 6;

    private static final int DEFAULT_WEEK_START = Calendar.SUNDAY;

    private final TextPaint mDayPaint = new TextPaint();
    private final Paint mDaySelectorPaint = new Paint();
    private final Paint mDayHighlightPaint = new Paint();

    private final Calendar mCalendar = Calendar.getInstance();

    private final MonthViewTouchHelper mTouchHelper;

    private CharSequence mTitle;

    private int mMonth;
    private int mYear;

    // Dimensions as laid out.
    private int mMonthHeight;
    private int mDayOfWeekHeight;
    private int mDayHeight;
    private int mCellWidth;
    private int mDaySelectorRadius;

    private int mPaddedWidth;
    private int mPaddedHeight;

    /**
     * The day of month for the selected day, or -1 if no day is selected.
     */
    private int mActivatedDay = -1;

    /**
     * The day of month for today, or -1 if the today is not in the current
     * month.
     */
//    private int mToday = DEFAULT_SELECTED_DAY;

    /**
     * The first day of the week (ex. Calendar.SUNDAY).
     */
    private int mWeekStart = DEFAULT_WEEK_START;

    /**
     * The number of days (ex. 28) in the current month.
     */
    private int mDaysInMonth;

    /**
     * The day of week (ex. Calendar.SUNDAY) for the first day of the current
     * month.
     */
    private int mDayOfWeekStart;

    /**
     * The day of month for the first (inclusive) enabled day.
     */
    private int mEnabledDayStart = 1;

    /**
     * The day of month for the last (inclusive) enabled day.
     */
    private int mEnabledDayEnd = 31;

    /**
     * Optional listener for handling day click actions.
     */
    private OnDayClickListener mOnDayClickListener;

    private ColorStateList mDayTextColor;

    private int mTouchedItem = -1;
    private StyleDelegate mStyleDelegate;

    public SimpleMonthView(Context context) {
        this(context, null);
    }

    public SimpleMonthView(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.mcv_calendarViewStyle);
    }

    public SimpleMonthView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // Set up accessibility components.
        mTouchHelper = new MonthViewTouchHelper(this);
        ViewCompat.setAccessibilityDelegate(this, mTouchHelper);
        ViewCompat.setImportantForAccessibility(this, ViewCompat.IMPORTANT_FOR_ACCESSIBILITY_YES);

        initPaints(getResources());
    }

    public int getPaddedWidth() {
        return mPaddedWidth;
    }

    public int getMonthHeight() {
        return mMonthHeight;
    }

    public int getCellWidth() {
        return mCellWidth;
    }

    public int getDayOfWeekHeight() {
        return mDayOfWeekHeight;
    }

    public void setDayTextAppearance(int resId) {
        final ColorStateList textColor = ViewUtils.applyTextAppearance(getContext(), mDayPaint, resId);
        if (textColor != null) {
            mDayTextColor = textColor;
        }

        invalidate();
    }

    public CharSequence getTitle() {
        if (mTitle == null) {
            mTitle = mStyleDelegate.getMonthLabel(mCalendar.getTime());
        }
        return mTitle;
    }

    /**
     * Sets up the text and style properties for painting.
     */
    private void initPaints(Resources res) {
        final int dayOfWeekTextSize = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_day_of_week_text_size);
        final int dayTextSize = res.getDimensionPixelSize(
                R.dimen.mcv_date_picker_day_text_size);

        mDaySelectorPaint.setAntiAlias(true);
        mDaySelectorPaint.setStyle(Style.FILL);

        mDayHighlightPaint.setAntiAlias(true);
        mDayHighlightPaint.setStyle(Style.FILL);

        mDayPaint.setAntiAlias(true);
        mDayPaint.setTextSize(dayTextSize);
        mDayPaint.setTypeface(Typeface.DEFAULT);
        mDayPaint.setTextAlign(Align.CENTER);
        mDayPaint.setStyle(Style.FILL);
    }

    public void setStyleDelegate(StyleDelegate styleDelegate) {
        mStyleDelegate = styleDelegate;

        setDayTextAppearance(mStyleDelegate.getDayTextAppearance());
        setDaySelectorColor(mStyleDelegate.getSelectionColor());
        setDayHighlightColor(mStyleDelegate.getHighlightColor());

        invalidate();
    }

    void setDaySelectorColor(ColorStateList dayBackgroundColor) {
        final int activatedColor = dayBackgroundColor.getColorForState(ViewUtils.STATE_ACTIVATED, 0);
        mDaySelectorPaint.setColor(activatedColor);
        invalidate();
    }

    void setDayHighlightColor(ColorStateList dayHighlightColor) {
        final int pressedColor = dayHighlightColor.getColorForState(ViewUtils.STATE_PRESSED, 0);
        mDayHighlightPaint.setColor(pressedColor);
        invalidate();
    }

    public void setOnDayClickListener(OnDayClickListener listener) {
        mOnDayClickListener = listener;
    }

    @Override
    public boolean dispatchHoverEvent(MotionEvent event) {
        // First right-of-refusal goes the touch exploration helper.
        return mTouchHelper.dispatchHoverEvent(event) || super.dispatchHoverEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int x = (int) (event.getX() + 0.5f);
        final int y = (int) (event.getY() + 0.5f);

        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                final int touchedItem = getDayAtLocation(x, y);
                if (mTouchedItem != touchedItem) {
                    mTouchedItem = touchedItem;
                    invalidate();
                }
                if (action == MotionEvent.ACTION_DOWN && touchedItem < 0) {
                    // Touch something that's not an item, reject event.
                    return false;
                }
                break;

            case MotionEvent.ACTION_UP:
                final int clickedDay = getDayAtLocation(x, y);
                onDayClicked(clickedDay);
                // Fall through.
            case MotionEvent.ACTION_CANCEL:
                // Reset touched day on stream end.
                mTouchedItem = -1;
                invalidate();
                break;
        }
        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        canvas.translate(paddingLeft, paddingTop);

        mStyleDelegate.drawMonth(this, canvas);
        mStyleDelegate.drawDaysOfWeek(this, canvas);
        drawDays(canvas);

        canvas.translate(-paddingLeft, -paddingTop);
    }

    /**
     * Draws the month days.
     */
    private void drawDays(Canvas canvas) {
        final TextPaint p = mDayPaint;
        final int headerHeight = mMonthHeight + mDayOfWeekHeight;
        final int rowHeight = mDayHeight;
        final int colWidth = mCellWidth;

        // Text is vertically centered within the row height.
        final float halfLineHeight = (p.ascent() + p.descent()) / 2f;
        int rowCenter = headerHeight + rowHeight / 2;

        for (int day = 1, col = findDayOffset(); day <= mDaysInMonth; day++) {
            final int colCenter = colWidth * col + colWidth / 2;
            final int colCenterRtl;
            if (ViewUtils.isLayoutRtl(this)) {
                colCenterRtl = mPaddedWidth - colCenter;
            } else {
                colCenterRtl = colCenter;
            }

            int[] stateMask;

            final boolean isDayEnabled = isDayEnabled(day);

            final boolean isDayActivated = mActivatedDay == day;
            if (isDayActivated) {
                stateMask = ViewUtils.STATE_ACTIVATED;

                // Adjust the circle to be centered on the row.
                mStyleDelegate.drawDaySelected(canvas, mDaySelectorPaint,
                        colCenterRtl, rowCenter,
                        mDaySelectorRadius);

            } else if (mTouchedItem == day) {
                stateMask = ViewUtils.STATE_PRESSED;

                if (isDayEnabled) {
                    // Adjust the circle to be centered on the row.
                    mStyleDelegate.drawDayPressed(canvas, mDayHighlightPaint,
                            colCenterRtl, rowCenter,
                            mDaySelectorRadius);
                }
            } else {
                stateMask = isDayEnabled ? ViewUtils.STATE_ENALBED : ViewUtils.STATE_DISALBED;
            }

            p.setColor(mDayTextColor.getColorForState(stateMask, 0));

            mStyleDelegate.drawDayLabel(canvas, p, mStyleDelegate.getDayLabel(day), colCenterRtl, rowCenter - halfLineHeight);

            col++;

            if (col == DAYS_IN_WEEK) {
                col = 0;
                rowCenter += rowHeight;
            }
        }
    }

    private boolean isDayEnabled(int day) {
        return day >= mEnabledDayStart && day <= mEnabledDayEnd;
    }

    private boolean isValidDayOfMonth(int day) {
        return day >= 1 && day <= mDaysInMonth;
    }

    private static boolean isValidDayOfWeek(int day) {
        return day >= Calendar.SUNDAY && day <= Calendar.SATURDAY;
    }

    private static boolean isValidMonth(int month) {
        return month >= Calendar.JANUARY && month <= Calendar.DECEMBER;
    }

    /**
     * Sets the selected day.
     *
     * @param dayOfMonth the selected day of the month, or {@code -1} to clear
     *                   the selection
     */
    public void setSelectedDay(int dayOfMonth) {
        mActivatedDay = dayOfMonth;

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot();
        invalidate();
    }

    /**
     * Sets the first day of the week.
     *
     * @param weekStart which day the week should start on, valid values are
     *                  {@link Calendar#SUNDAY} through {@link Calendar#SATURDAY}
     */
    public void setFirstDayOfWeek(int weekStart) {
        if (isValidDayOfWeek(weekStart)) {
            mWeekStart = weekStart;
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot();
        invalidate();
    }

    /**
     * Sets all the parameters for displaying this week.
     * <p/>
     * Parameters have a default value and will only update if a new value is
     * included, except for focus month, which will always default to no focus
     * month if no value is passed in. The only required parameter is the week
     * start.
     *
     * @param selectedDay     the selected day of the month, or -1 for no selection
     * @param month           the month
     * @param year            the year
     * @param weekStart       which day the week should start on, valid values are
     *                        {@link Calendar#SUNDAY} through {@link Calendar#SATURDAY}
     * @param enabledDayStart the first enabled day
     * @param enabledDayEnd   the last enabled day
     */
    void setMonthParams(int selectedDay, int month, int year, int weekStart, int enabledDayStart,
                        int enabledDayEnd) {
        mActivatedDay = selectedDay;

        if (isValidMonth(month)) {
            mMonth = month;
        }
        mYear = year;

        mCalendar.set(Calendar.MONTH, mMonth);
        mCalendar.set(Calendar.YEAR, mYear);
        mCalendar.set(Calendar.DAY_OF_MONTH, 1);
        mDayOfWeekStart = mCalendar.get(Calendar.DAY_OF_WEEK);

        if (isValidDayOfWeek(weekStart)) {
            mWeekStart = weekStart;
        } else {
            mWeekStart = mCalendar.getFirstDayOfWeek();
        }

        mDaysInMonth = getDaysInMonth(mMonth, mYear);

        mEnabledDayStart = MathUtils.constrain(enabledDayStart, 1, mDaysInMonth);
        mEnabledDayEnd = MathUtils.constrain(enabledDayEnd, mEnabledDayStart, mDaysInMonth);

        // Invalidate the old title.
        mTitle = null;

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot();
    }

    private static int getDaysInMonth(int month, int year) {
        switch (month) {
            case Calendar.JANUARY:
            case Calendar.MARCH:
            case Calendar.MAY:
            case Calendar.JULY:
            case Calendar.AUGUST:
            case Calendar.OCTOBER:
            case Calendar.DECEMBER:
                return 31;
            case Calendar.APRIL:
            case Calendar.JUNE:
            case Calendar.SEPTEMBER:
            case Calendar.NOVEMBER:
                return 30;
            case Calendar.FEBRUARY:
                return (year % 4 == 0) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid Month");
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int preferredHeight = mStyleDelegate.getDesiredDayHeight() * MAX_WEEKS_IN_MONTH
                + mStyleDelegate.getDesiredDayOfWeekHeight()
                + mStyleDelegate.getDesiredMonthHeight()
                + getPaddingTop() + getPaddingBottom();
        final int preferredWidth = mStyleDelegate.getDesiredCellWidth() * DAYS_IN_WEEK
                + ViewCompat.getPaddingStart(this) + ViewCompat.getPaddingEnd(this);
        final int resolvedWidth = resolveSize(preferredWidth, widthMeasureSpec);
        final int resolvedHeight = resolveSize(preferredHeight, heightMeasureSpec);
        setMeasuredDimension(resolvedWidth, resolvedHeight);
    }

    @Override
    public void onRtlPropertiesChanged(int layoutDirection) {
        super.onRtlPropertiesChanged(layoutDirection);

        requestLayout();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        if (!changed) {
            return;
        }

        // Let's initialize a completely reasonable number of variables.
        final int w = right - left;
        final int h = bottom - top;
        final int paddingLeft = getPaddingLeft();
        final int paddingTop = getPaddingTop();
        final int paddingRight = getPaddingRight();
        final int paddingBottom = getPaddingBottom();
        final int paddedRight = w - paddingRight;
        final int paddedBottom = h - paddingBottom;
        final int paddedWidth = paddedRight - paddingLeft;
        final int paddedHeight = paddedBottom - paddingTop;
        if (paddedWidth == mPaddedWidth || paddedHeight == mPaddedHeight) {
            return;
        }

        mPaddedWidth = paddedWidth;
        mPaddedHeight = paddedHeight;

        // We may have been laid out smaller than our preferred size. If so,
        // scale all dimensions to fit.
        final int measuredPaddedHeight = getMeasuredHeight() - paddingTop - paddingBottom;
        final float scaleH = paddedHeight / (float) measuredPaddedHeight;
        final int monthHeight = (int) (mStyleDelegate.getDesiredMonthHeight() * scaleH);
        final int cellWidth = mPaddedWidth / DAYS_IN_WEEK;
        mMonthHeight = monthHeight;
        mDayOfWeekHeight = (int) (mStyleDelegate.getDesiredDayOfWeekHeight() * scaleH);
        mDayHeight = (int) (mStyleDelegate.getDesiredDayHeight() * scaleH);
        mCellWidth = cellWidth;

        // Compute the largest day selector radius that's still within the clip
        // bounds and desired selector radius.
        final int maxSelectorWidth = cellWidth / 2 + Math.min(paddingLeft, paddingRight);
        final int maxSelectorHeight = mDayHeight / 2 + paddingBottom;
        mDaySelectorRadius = Math.min(mStyleDelegate.getDesiredDaySelectorRadius(),
                Math.min(maxSelectorWidth, maxSelectorHeight));

        // Invalidate cached accessibility information.
        mTouchHelper.invalidateRoot();
    }

    private int findDayOffset() {
        final int offset = mDayOfWeekStart - mWeekStart;
        if (mDayOfWeekStart < mWeekStart) {
            return offset + DAYS_IN_WEEK;
        }
        return offset;
    }

    /**
     * Calculates the day of the month at the specified touch position. Returns
     * the day of the month or -1 if the position wasn't in a valid day.
     *
     * @param x the x position of the touch event
     * @param y the y position of the touch event
     * @return the day of the month at (x, y), or -1 if the position wasn't in
     * a valid day
     */
    private int getDayAtLocation(int x, int y) {
        final int paddedX = x - getPaddingLeft();
        if (paddedX < 0 || paddedX >= mPaddedWidth) {
            return -1;
        }

        final int headerHeight = mMonthHeight + mDayOfWeekHeight;
        final int paddedY = y - getPaddingTop();
        if (paddedY < headerHeight || paddedY >= mPaddedHeight) {
            return -1;
        }

        // Adjust for RTL after applying padding.
        final int paddedXRtl;
        if (ViewUtils.isLayoutRtl(this)) {
            paddedXRtl = mPaddedWidth - paddedX;
        } else {
            paddedXRtl = paddedX;
        }

        final int row = (paddedY - headerHeight) / mDayHeight;
        final int col = (paddedXRtl * DAYS_IN_WEEK) / mPaddedWidth;
        final int index = col + row * DAYS_IN_WEEK;
        final int day = index + 1 - findDayOffset();
        if (!isValidDayOfMonth(day)) {
            return -1;
        }

        return day;
    }

    /**
     * Calculates the bounds of the specified day.
     *
     * @param id        the day of the month
     * @param outBounds the rect to populate with bounds
     */
    private boolean getBoundsForDay(int id, Rect outBounds) {
        if (!isValidDayOfMonth(id)) {
            return false;
        }

        final int index = id - 1 + findDayOffset();

        // Compute left edge, taking into account RTL.
        final int col = index % DAYS_IN_WEEK;
        final int colWidth = mCellWidth;
        final int left;
        if (ViewUtils.isLayoutRtl(this)) {
            left = getWidth() - getPaddingRight() - (col + 1) * colWidth;
        } else {
            left = getPaddingLeft() + col * colWidth;
        }

        // Compute top edge.
        final int row = index / DAYS_IN_WEEK;
        final int rowHeight = mDayHeight;
        final int headerHeight = mMonthHeight + mDayOfWeekHeight;
        final int top = getPaddingTop() + headerHeight + row * rowHeight;

        outBounds.set(left, top, left + colWidth, top + rowHeight);

        return true;
    }

    /**
     * Called when the user clicks on a day. Handles callbacks to the
     * {@link OnDayClickListener} if one is set.
     *
     * @param day the day that was clicked
     */
    private boolean onDayClicked(int day) {
        if (!isValidDayOfMonth(day) || !isDayEnabled(day)) {
            return false;
        }

        if (mOnDayClickListener != null) {
            final Calendar date = Calendar.getInstance();
            date.set(mYear, mMonth, day);
            mOnDayClickListener.onDayClick(this, date);
        }

        // This is a no-op if accessibility is turned off.
        mTouchHelper.sendEventForVirtualView(day, AccessibilityEvent.TYPE_VIEW_CLICKED);
        return true;
    }

    /**
     * Provides a virtual view hierarchy for interfacing with an accessibility
     * service.
     */
    private class MonthViewTouchHelper extends ExploreByTouchHelper {
        private static final String DATE_FORMAT = "dd MMMM yyyy";

        private final Rect mTempRect = new Rect();
        private final Calendar mTempCalendar = Calendar.getInstance();

        public MonthViewTouchHelper(View host) {
            super(host);
        }

        @Override
        protected int getVirtualViewAt(float x, float y) {
            final int day = getDayAtLocation((int) (x + 0.5f), (int) (y + 0.5f));
            if (day != -1) {
                return day;
            }
            return ExploreByTouchHelper.INVALID_ID;
        }

        @Override
        protected void getVisibleVirtualViews(List<Integer> virtualViewIds) {
            for (int day = 1; day <= mDaysInMonth; day++) {
                virtualViewIds.add(day);
            }
        }

        @Override
        protected void onPopulateEventForVirtualView(int virtualViewId, AccessibilityEvent event) {
            event.setContentDescription(getDayDescription(virtualViewId));
        }

        @Override
        protected void onPopulateNodeForVirtualView(int virtualViewId, AccessibilityNodeInfoCompat node) {
            final boolean hasBounds = getBoundsForDay(virtualViewId, mTempRect);

            if (!hasBounds) {
                // The day is invalid, kill the node.
                mTempRect.setEmpty();
                node.setContentDescription("");
                node.setBoundsInParent(mTempRect);
                node.setVisibleToUser(false);
                return;
            }

            node.setText(getDayText(virtualViewId));
            node.setContentDescription(getDayDescription(virtualViewId));
            node.setBoundsInParent(mTempRect);

            final boolean isDayEnabled = isDayEnabled(virtualViewId);
            if (isDayEnabled) {
                node.addAction(AccessibilityNodeInfoCompat.AccessibilityActionCompat.ACTION_CLICK);
            }

            node.setEnabled(isDayEnabled);

            if (virtualViewId == mActivatedDay) {
                // TODO: This should use activated once that's supported.
                node.setChecked(true);
            }

        }

        @Override
        protected boolean onPerformActionForVirtualView(int virtualViewId, int action,
                                                        Bundle arguments) {
            switch (action) {
                case AccessibilityNodeInfo.ACTION_CLICK:
                    return onDayClicked(virtualViewId);
            }

            return false;
        }

        /**
         * Generates a description for a given virtual view.
         *
         * @param id the day to generate a description for
         * @return a description of the virtual view
         */
        private CharSequence getDayDescription(int id) {
            if (isValidDayOfMonth(id)) {
                mTempCalendar.set(mYear, mMonth, id);
                return DateFormat.format(DATE_FORMAT, mTempCalendar.getTimeInMillis());
            }

            return "";
        }

        /**
         * Generates displayed text for a given virtual view.
         *
         * @param id the day to generate text for
         * @return the visible text of the virtual view
         */
        private CharSequence getDayText(int id) {
            if (isValidDayOfMonth(id)) {
                return mStyleDelegate.getDayLabel(id);
            }

            return null;
        }
    }

    /**
     * Handles callbacks when the user clicks on a time object.
     */
    public interface OnDayClickListener {
        void onDayClick(SimpleMonthView view, Calendar day);
    }
}
