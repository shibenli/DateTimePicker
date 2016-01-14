package com.lany.picker.datetimepicker;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.text.format.DateUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.lany.picker.R;
import com.lany.picker.datepicker.CVArrays;
import com.lany.picker.numberpicker.NumberPicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * custom year/month/day/hour picker
 */
public class YmdhmPicker extends FrameLayout {
    private static final String LOG_TAG = YmdhmPicker.class.getSimpleName();
    private static final String DATE_FORMAT = "MM/dd/yyyy";
    private static final int DEFAULT_START_YEAR = 1900;
    private static final int DEFAULT_END_YEAR = 2100;

    private NumberPicker mMinuteSpinner;
    private NumberPicker mHourSpinner;
    private NumberPicker mDaySpinner;
    private NumberPicker mMonthSpinner;
    private NumberPicker mYearSpinner;

    private final EditText mMinuteSpinnerInput;
    private final EditText mHourSpinnerInput;
    private final EditText mDaySpinnerInput;
    private final EditText mMonthSpinnerInput;
    private final EditText mYearSpinnerInput;

    private Locale mCurrentLocale;

    private OnDateChangedListener mOnDateChangedListener;

    private String[] mShortMonths;

    private final java.text.DateFormat mDateFormat = new SimpleDateFormat(	DATE_FORMAT,Locale.getDefault());

    private int mNumberOfMonths;

    private Calendar mTempDate;
    private Calendar mMinDate;
    private Calendar mMaxDate;
    private Calendar mCurrentDate;

    private boolean mIsEnabled = true;

    public interface OnDateChangedListener {
        void onDateChanged(YmdhmPicker view, int year, int monthOfYear,
                           int dayOfMonth, int hourOfDay, int minuteOfHour);
    }

    public void setOnDateChangedListener(
            OnDateChangedListener onDateChangedListener) {
        mOnDateChangedListener = onDateChangedListener;
    }

    public YmdhmPicker(Context context) {
        this(context, null);
    }

    public YmdhmPicker(Context context, AttributeSet attrs) {
        this(context, attrs, R.attr.datePickerStyle);
    }

    public YmdhmPicker(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setCurrentLocale(Locale.getDefault());
        TypedArray attributesArray = context.obtainStyledAttributes(attrs,
                R.styleable.DatePicker, defStyle, 0);
        int startYear = attributesArray.getInt(
                R.styleable.DatePicker_dp_startYear, DEFAULT_START_YEAR);
        int endYear = attributesArray.getInt(R.styleable.DatePicker_dp_endYear,
                DEFAULT_END_YEAR);
        String minDate = attributesArray
                .getString(R.styleable.DatePicker_dp_minDate);
        String maxDate = attributesArray
                .getString(R.styleable.DatePicker_dp_maxDate);
        attributesArray.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.ymdhm_picker_holo, this, true);

        NumberPicker.OnValueChangeListener onChangeListener = new NumberPicker.OnValueChangeListener() {
            public void onValueChange(NumberPicker picker, int oldVal,
                                      int newVal) {
                updateInputState();
                mTempDate.setTimeInMillis(mCurrentDate.getTimeInMillis());
                // take care of wrapping of days and months to update greater
                // fields
                if (picker == mDaySpinner) {
                    int maxDayOfMonth = mTempDate
                            .getActualMaximum(Calendar.DAY_OF_MONTH);
                    if (oldVal == maxDayOfMonth && newVal == 1) {
                        mTempDate.add(Calendar.DAY_OF_MONTH, 1);
                    } else if (oldVal == 1 && newVal == maxDayOfMonth) {
                        mTempDate.add(Calendar.DAY_OF_MONTH, -1);
                    } else {
                        mTempDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
                    }
                } else if (picker == mMonthSpinner) {
                    if (oldVal == 11 && newVal == 0) {
                        mTempDate.add(Calendar.MONTH, 1);
                    } else if (oldVal == 0 && newVal == 11) {
                        mTempDate.add(Calendar.MONTH, -1);
                    } else {
                        mTempDate.add(Calendar.MONTH, newVal - oldVal);
                    }
                } else if (picker == mYearSpinner) {
                    mTempDate.set(Calendar.YEAR, newVal);
                } else if (picker == mHourSpinner) {
                    mTempDate.set(Calendar.HOUR_OF_DAY, newVal);
                } else if (picker == mMinuteSpinner) {
                    mTempDate.set(Calendar.MINUTE, newVal);
                } else {
                    throw new IllegalArgumentException();
                }
                // now set the date to the adjusted one
                setDate(mTempDate.get(Calendar.YEAR),
                        mTempDate.get(Calendar.MONTH),
                        mTempDate.get(Calendar.DAY_OF_MONTH),
                        mTempDate.get(Calendar.HOUR_OF_DAY),
                        mTempDate.get(Calendar.MINUTE));
                updateSpinners();
                notifyDateChanged();
            }
        };

        // minute
        mMinuteSpinner = (NumberPicker) findViewById(R.id.minute);
        mMinuteSpinner.setOnLongPressUpdateInterval(100);
        mMinuteSpinner.setOnValueChangedListener(onChangeListener);
        mMinuteSpinnerInput = (EditText) mMinuteSpinner
                .findViewById(R.id.np__numberpicker_input);

        // hour
        mHourSpinner = (NumberPicker) findViewById(R.id.hour);
        mHourSpinner.setOnLongPressUpdateInterval(100);
        mHourSpinner.setOnValueChangedListener(onChangeListener);
        mHourSpinnerInput = (EditText) mHourSpinner
                .findViewById(R.id.np__numberpicker_input);

        // day
        mDaySpinner = (NumberPicker) findViewById(R.id.day);
        mDaySpinner.setFormatter(NumberPicker.getTwoDigitFormatter());
        mDaySpinner.setOnLongPressUpdateInterval(100);
        mDaySpinner.setOnValueChangedListener(onChangeListener);
        mDaySpinnerInput = (EditText) mDaySpinner
                .findViewById(R.id.np__numberpicker_input);
        // month
        mMonthSpinner = (NumberPicker) findViewById(R.id.month);
        mMonthSpinner.setMinValue(0);
        mMonthSpinner.setMaxValue(mNumberOfMonths - 1);
        mMonthSpinner.setDisplayedValues(mShortMonths);
        mMonthSpinner.setOnLongPressUpdateInterval(200);
        mMonthSpinner.setOnValueChangedListener(onChangeListener);
        mMonthSpinnerInput = (EditText) mMonthSpinner
                .findViewById(R.id.np__numberpicker_input);

        // year
        mYearSpinner = (NumberPicker) findViewById(R.id.year);
        mYearSpinner.setOnLongPressUpdateInterval(100);
        mYearSpinner.setOnValueChangedListener(onChangeListener);
        mYearSpinnerInput = (EditText) mYearSpinner
                .findViewById(R.id.np__numberpicker_input);

        // set the min date giving priority of the minDate over startYear
        mTempDate.clear();
        if (!TextUtils.isEmpty(minDate)) {
            if (!parseDate(minDate, mTempDate)) {
                mTempDate.set(startYear, 0, 1);
            }
        } else {
            mTempDate.set(startYear, 0, 1);
        }
        setMinDate(mTempDate.getTimeInMillis());

        // set the max date giving priority of the maxDate over endYear
        mTempDate.clear();
        if (!TextUtils.isEmpty(maxDate)) {
            if (!parseDate(maxDate, mTempDate)) {
                mTempDate.set(endYear, 11, 31);
            }
        } else {
            mTempDate.set(endYear, 11, 31);
        }
        setMaxDate(mTempDate.getTimeInMillis());

        // initialize to current date
        mCurrentDate.setTimeInMillis(System.currentTimeMillis());
        init(mCurrentDate.get(Calendar.YEAR), mCurrentDate.get(Calendar.MONTH),
                mCurrentDate.get(Calendar.DAY_OF_MONTH),
                mCurrentDate.get(Calendar.HOUR_OF_DAY), mCurrentDate.get(Calendar.MINUTE), null);

        // re-order the number spinners to match the current date format
        reorderSpinners();

        // If not explicitly specified this view is important for accessibility.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN
                && getImportantForAccessibility() == IMPORTANT_FOR_ACCESSIBILITY_AUTO) {
            setImportantForAccessibility(IMPORTANT_FOR_ACCESSIBILITY_YES);
        }
    }

    public void setSelectionDivider(Drawable selectionDivider) {
        mDaySpinner.setSelectionDivider(selectionDivider);
        mMonthSpinner.setSelectionDivider(selectionDivider);
        mYearSpinner.setSelectionDivider(selectionDivider);
        mHourSpinner.setSelectionDivider(selectionDivider);
        mMinuteSpinner.setSelectionDivider(selectionDivider);
    }

    public void setSelectionDividerHeight(int selectionDividerHeight) {
        mDaySpinner.setSelectionDividerHeight(selectionDividerHeight);
        mMonthSpinner.setSelectionDividerHeight(selectionDividerHeight);
        mYearSpinner.setSelectionDividerHeight(selectionDividerHeight);
        mHourSpinner.setSelectionDividerHeight(selectionDividerHeight);
        mMinuteSpinner.setSelectionDividerHeight(selectionDividerHeight);
    }

    public void setMinDate(long minDate) {
        mTempDate.setTimeInMillis(minDate);
        if (mTempDate.get(Calendar.YEAR) == mMinDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMinDate
                .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMinDate.setTimeInMillis(minDate);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        }
        updateSpinners();
    }

    public void setMaxDate(long maxDate) {
        mTempDate.setTimeInMillis(maxDate);
        if (mTempDate.get(Calendar.YEAR) == mMaxDate.get(Calendar.YEAR)
                && mTempDate.get(Calendar.DAY_OF_YEAR) != mMaxDate
                .get(Calendar.DAY_OF_YEAR)) {
            return;
        }
        mMaxDate.setTimeInMillis(maxDate);
        if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
        updateSpinners();
    }

    @Override
    public void setEnabled(boolean enabled) {
        if (mIsEnabled == enabled) {
            return;
        }
        super.setEnabled(enabled);
        mMinuteSpinner.setEnabled(enabled);
        mHourSpinner.setEnabled(enabled);
        mDaySpinner.setEnabled(enabled);
        mMonthSpinner.setEnabled(enabled);
        mYearSpinner.setEnabled(enabled);
        mIsEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return mIsEnabled;
    }

    /**
     * Set the descendant focusability of this view group. This defines the relationship
     * between this view group and its descendants when looking for a view to
     * take focus in {@link #requestFocus(int, android.graphics.Rect)}.
     *
     * @param focusability one of {@link #FOCUS_BEFORE_DESCENDANTS}, {@link #FOCUS_AFTER_DESCENDANTS},
     *   {@link #FOCUS_BLOCK_DESCENDANTS}.
     */
    public void setDescendantFocusability(int focusability){
        super.setDescendantFocusability(focusability);
        if (mMinuteSpinner != null) {
            mMinuteSpinner.setDescendantFocusability(focusability);
        }
        if (mHourSpinner != null) {
            mHourSpinner.setDescendantFocusability(focusability);
        }
        if (mDaySpinner != null) {
            mDaySpinner.setDescendantFocusability(focusability);
        }
        if (mMonthSpinner != null) {
            mMonthSpinner.setDescendantFocusability(focusability);
        }
        if (mYearSpinner != null) {
            mYearSpinner.setDescendantFocusability(focusability);
        }
    }

    protected boolean useHour;
    protected boolean useMinute;

    /**
     * display hour
     *
     * @param useHour
     */
    public void setUseHour(boolean useHour) {
        this.useHour = useHour;
    }

    /**
     * display hour
     *
     * @param useMinute
     */
    public void setUseMinute(boolean useMinute) {
        this.useMinute = useMinute;
    }

    public boolean isUseHour() {
        return useHour;
    }

    public boolean isUseMinute() {
        return useMinute;
    }

    @Override
    public boolean dispatchPopulateAccessibilityEvent(AccessibilityEvent event) {
        onPopulateAccessibilityEvent(event);
        return true;
    }

    @Override
    public void onPopulateAccessibilityEvent(AccessibilityEvent event) {
        super.onPopulateAccessibilityEvent(event);

        final int flags = DateUtils.FORMAT_SHOW_DATE
                | DateUtils.FORMAT_SHOW_YEAR;
        String selectedDateUtterance = DateUtils.formatDateTime(getContext(),
                mCurrentDate.getTimeInMillis(), flags);
        event.getText().add(selectedDateUtterance);
    }

    @Override
    public void onInitializeAccessibilityEvent(AccessibilityEvent event) {
        super.onInitializeAccessibilityEvent(event);
        event.setClassName(YmdhmPicker.class.getName());
    }

    @Override
    public void onInitializeAccessibilityNodeInfo(AccessibilityNodeInfo info) {
        super.onInitializeAccessibilityNodeInfo(info);
        info.setClassName(YmdhmPicker.class.getName());
    }

    @Override
    protected void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setCurrentLocale(newConfig.locale);
    }

    private void setCurrentLocale(Locale locale) {
        if (locale.equals(mCurrentLocale)) {
            return;
        }
        mCurrentLocale = locale;
        mTempDate = getCalendarForLocale(mTempDate, locale);
        mMinDate = getCalendarForLocale(mMinDate, locale);
        mMaxDate = getCalendarForLocale(mMaxDate, locale);
        mCurrentDate = getCalendarForLocale(mCurrentDate, locale);

        mNumberOfMonths = mTempDate.getActualMaximum(Calendar.MONTH) + 1;
        mShortMonths = new String[mNumberOfMonths];
        for (int i = 0; i < mNumberOfMonths; i++) {
            mShortMonths[i] = DateUtils.getMonthString(Calendar.JANUARY + i,
                    DateUtils.LENGTH_MEDIUM);
        }
    }

    private Calendar getCalendarForLocale(Calendar oldCalendar, Locale locale) {
        if (oldCalendar == null) {
            return Calendar.getInstance(locale);
        } else {
            final long currentTimeMillis = oldCalendar.getTimeInMillis();
            Calendar newCalendar = Calendar.getInstance(locale);
            newCalendar.setTimeInMillis(currentTimeMillis);
            return newCalendar;
        }
    }

    private void reorderSpinners() {
        char[] order = DateFormat.getDateFormatOrder(getContext());
        final int spinnerCount = order.length;
        for (int i = 0; i < spinnerCount; i++) {
            switch (order[i]) {
                case 'm':
                    setImeOptions(mMinuteSpinner, spinnerCount, i);
                    break;
                case 'h':
                    setImeOptions(mHourSpinner, spinnerCount, i);
                    break;
                case 'd':
                    setImeOptions(mDaySpinner, spinnerCount, i);
                    break;
                case 'M':
                    setImeOptions(mMonthSpinner, spinnerCount, i);
                    break;
                case 'y':
                    setImeOptions(mYearSpinner, spinnerCount, i);
                    break;
                default:
                    throw new IllegalArgumentException();
            }
        }
    }

    public void updateDate(int year, int month, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        if (!isNewDate(year, month, dayOfMonth, hourOfDay, minuteOfHour)) {
            return;
        }
        setDate(year, month, dayOfMonth, hourOfDay, minuteOfHour);
        updateSpinners();
        notifyDateChanged();
    }

    @Override
    protected void dispatchRestoreInstanceState(
            SparseArray<Parcelable> container) {
        dispatchThawSelfOnly(container);
    }

    @Override
    protected Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        return new SavedState(superState, getYear(), getMonth(),
                getDayOfMonth(), getHourOfDay(), getMinuteOfHour());
    }

    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        SavedState ss = (SavedState) state;
        super.onRestoreInstanceState(ss.getSuperState());
        setDate(ss.mYear, ss.mMonth, ss.mDay, ss.mHour, ss.mMinute);
        updateSpinners();
    }

    public void init(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour,
                     OnDateChangedListener onDateChangedListener) {
        setDate(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
        updateSpinners();
        mOnDateChangedListener = onDateChangedListener;
    }

    private boolean parseDate(String date, Calendar outDate) {
        try {
            outDate.setTime(mDateFormat.parse(date));
            return true;
        } catch (ParseException e) {
            Log.w(LOG_TAG, "Date: " + date + " not in format: " + DATE_FORMAT);
            return false;
        }
    }

    private boolean isNewDate(int year, int month, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        return (mCurrentDate.get(Calendar.YEAR) != year
                || mCurrentDate.get(Calendar.MONTH) != dayOfMonth
                || mCurrentDate.get(Calendar.DAY_OF_MONTH) != month || mCurrentDate
                .get(Calendar.HOUR_OF_DAY) != hourOfDay || mCurrentDate.get(Calendar.MINUTE) != minuteOfHour);
    }

    private void setDate(int year, int month, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        mCurrentDate.set(year, month, dayOfMonth, hourOfDay, minuteOfHour);
        if (mCurrentDate.before(mMinDate)) {
            mCurrentDate.setTimeInMillis(mMinDate.getTimeInMillis());
        } else if (mCurrentDate.after(mMaxDate)) {
            mCurrentDate.setTimeInMillis(mMaxDate.getTimeInMillis());
        }
    }

    protected void updateSpinners() {
//        if (mCurrentDate.equals(mMinDate)) {
//            mDaySpinner.setMinValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
//            mDaySpinner.setMaxValue(mCurrentDate
//                    .getActualMaximum(Calendar.DAY_OF_MONTH));
//            mDaySpinner.setWrapSelectorWheel(false);
//            mMonthSpinner.setDisplayedValues(null);
//            mMonthSpinner.setMinValue(mCurrentDate.get(Calendar.MONTH));
//            mMonthSpinner.setMaxValue(mCurrentDate
//                    .getActualMaximum(Calendar.MONTH));
//            mMonthSpinner.setWrapSelectorWheel(false);
//        } else if (mCurrentDate.equals(mMaxDate)) {
//            mDaySpinner.setMinValue(mCurrentDate
//                    .getActualMinimum(Calendar.DAY_OF_MONTH));
//            mDaySpinner.setMaxValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
//            mDaySpinner.setWrapSelectorWheel(false);
//            mMonthSpinner.setDisplayedValues(null);
//            mMonthSpinner.setMinValue(mCurrentDate
//                    .getActualMinimum(Calendar.MONTH));
//            mMonthSpinner.setMaxValue(mCurrentDate.get(Calendar.MONTH));
//            mMonthSpinner.setWrapSelectorWheel(false);
//        } else {
            mDaySpinner.setMinValue(1);
            mDaySpinner.setMaxValue(mCurrentDate
                    .getActualMaximum(Calendar.DAY_OF_MONTH));
            mDaySpinner.setWrapSelectorWheel(true);
            mMonthSpinner.setDisplayedValues(null);
            mMonthSpinner.setMinValue(0);
            mMonthSpinner.setMaxValue(11);
            mMonthSpinner.setWrapSelectorWheel(true);
//        }

        mMinuteSpinner.setVisibility(useMinute ? VISIBLE : GONE);
        mHourSpinner.setVisibility(useHour ? VISIBLE : GONE);

        // make sure the month names are a zero based array
        // with the months in the month spinner
        String[] displayedValues = CVArrays.copyOfRange(mShortMonths,
                mMonthSpinner.getMinValue(), mMonthSpinner.getMaxValue() + 1);
        mMonthSpinner.setDisplayedValues(displayedValues);

        // year spinner range does not change based on the current date
//        mYearSpinner.setMinValue(mMinDate.get(Calendar.YEAR));
//        mYearSpinner.setMaxValue(mMaxDate.get(Calendar.YEAR));
        mYearSpinner.setMinValue(DEFAULT_START_YEAR);
        mYearSpinner.setMaxValue(DEFAULT_END_YEAR);
        mYearSpinner.setWrapSelectorWheel(false);

        mMinuteSpinner.setMinValue(0);
        mMinuteSpinner.setMaxValue(59);
        mMinuteSpinner.setWrapSelectorWheel(true);

        mHourSpinner.setMinValue(0);
        mHourSpinner.setMaxValue(23);
        mHourSpinner.setWrapSelectorWheel(true);

        // set the spinner values
        mYearSpinner.setValue(mCurrentDate.get(Calendar.YEAR));
        mMonthSpinner.setValue(mCurrentDate.get(Calendar.MONTH));
        mDaySpinner.setValue(mCurrentDate.get(Calendar.DAY_OF_MONTH));
        mHourSpinner.setValue(mCurrentDate.get(Calendar.HOUR_OF_DAY));
        mMinuteSpinner.setValue(mCurrentDate.get(Calendar.MINUTE));
    }

    /**
     * @return The selected year.
     */
    public int getYear() {
        return mCurrentDate.get(Calendar.YEAR);
    }

    /**
     * @return The selected month.
     */
    public int getMonth() {
        return mCurrentDate.get(Calendar.MONTH) + 1;
    }

    /**
     * @return The selected day of month.
     */
    public int getDayOfMonth() {
        return mCurrentDate.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return The selected hour of day.
     */
    public int getHourOfDay() {
        return useHour ? mCurrentDate.get(Calendar.HOUR_OF_DAY) : 0;
    }

    /**
     * @return The selected minute of hour.
     */
    public int getMinuteOfHour() {
        return useMinute ? mCurrentDate.get(Calendar.MINUTE) : 0;
    }

    /**
     * Notifies the listener, if such, for a change in the selected date.
     */
    private void notifyDateChanged() {
        sendAccessibilityEvent(AccessibilityEvent.TYPE_VIEW_SELECTED);
        if (mOnDateChangedListener != null) {
            mOnDateChangedListener.onDateChanged(this, getYear(), getMonth(),
                    getDayOfMonth(), getHourOfDay(), getMinuteOfHour());
        }
    }

    /**
     * Sets the IME options for a spinner based on its ordering.
     *
     * @param spinner
     *            The spinner.
     * @param spinnerCount
     *            The total spinner count.
     * @param spinnerIndex
     *            The index of the given spinner.
     */
    private void setImeOptions(NumberPicker spinner, int spinnerCount,
                               int spinnerIndex) {
        final int imeOptions;
        if (spinnerIndex < spinnerCount - 1) {
            imeOptions = EditorInfo.IME_ACTION_NEXT;
        } else {
            imeOptions = EditorInfo.IME_ACTION_DONE;
        }
        TextView input = (TextView) spinner
                .findViewById(R.id.np__numberpicker_input);
        input.setImeOptions(imeOptions);
    }

    private void updateInputState() {
        InputMethodManager inputMethodManager = (InputMethodManager) getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            if (inputMethodManager.isActive(mYearSpinnerInput)) {
                mYearSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mMonthSpinnerInput)) {
                mMonthSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mDaySpinnerInput)) {
                mDaySpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mHourSpinnerInput)) {
                mHourSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            } else if (inputMethodManager.isActive(mMinuteSpinnerInput)) {
                mMinuteSpinnerInput.clearFocus();
                inputMethodManager.hideSoftInputFromWindow(getWindowToken(), 0);
            }
        }
    }

    /**
     * Class for managing state storing/restoring.
     */
    private static class SavedState extends BaseSavedState {

        private final int mYear;

        private final int mMonth;

        private final int mDay;

        private final int mHour;

        private final int mMinute;

        /**
         * Constructor called from {@link YmdhmPicker#onSaveInstanceState()}
         */
        private SavedState(Parcelable superState, int year, int month, int day,
                           int hour, int minute) {
            super(superState);
            mYear = year;
            mMonth = month;
            mDay = day;
            mHour = hour;
            mMinute = minute;
        }

        /**
         * Constructor called from {@link #CREATOR}
         */
        private SavedState(Parcel in) {
            super(in);
            mYear = in.readInt();
            mMonth = in.readInt();
            mDay = in.readInt();
            mHour = in.readInt();
            mMinute = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(mYear);
            dest.writeInt(mMonth);
            dest.writeInt(mDay);
            dest.writeInt(mHour);
            dest.writeInt(mMinute);
        }

        @SuppressWarnings("all")
        // suppress unused and hiding
        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {

            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}