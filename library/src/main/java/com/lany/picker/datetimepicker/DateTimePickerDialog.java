package com.lany.picker.datetimepicker;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.lany.picker.R;
import com.lany.picker.datepicker.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Administrator on 2016/1/14.
 */
public class DateTimePickerDialog extends AlertDialog implements DialogInterface.OnClickListener,
        YmdhmPicker.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    private final YmdhmPicker mPicker;
    private final OnDateSetListener mCallBack;
    private final Calendar mCalendar;

    /**
     * The callback used to indicate the user is done filling in the date.
     */
    public interface OnDateSetListener {

        /**
         * @param view The view associated with this listener.
         * @param year The year that was set.
         * @param monthOfYear The month that was set (0-11) for compatibility
         *  with {@link java.util.Calendar}.
         * @param dayOfMonth The day of the month that was set.
         */
        void onDateSet(YmdhmPicker view, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param initDate The initial dialog by Calendar.
     */
    public DateTimePickerDialog(Context context,
                            OnDateSetListener callBack,
                            Calendar initDate) {
        this(context, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? R.style.Theme_Dialog_Alert : 0, callBack,
                initDate.get(Calendar.YEAR),
                initDate.get(Calendar.MONTH),
                initDate.get(Calendar.DAY_OF_MONTH),
                initDate.get(Calendar.HOUR_OF_DAY),
                initDate.get(Calendar.MINUTE));
    }
    /**
     * @param context The context the dialog is to run in.
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DateTimePickerDialog(Context context,
                            OnDateSetListener callBack,
                            int year,
                            int monthOfYear,
                            int dayOfMonth,
                            int hourOfDay,
                            int minuteOfHour) {
        this(context, Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? R.style.Theme_Dialog_Alert : 0, callBack, year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
    }

    /**
     * @param context The context the dialog is to run in.
     * @param theme the theme to apply to this dialog
     * @param callBack How the parent is notified that the date is set.
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    public DateTimePickerDialog(Context context,
                            int theme,
                            OnDateSetListener callBack,
                            int year,
                            int monthOfYear,
                            int dayOfMonth,
                            int hourOfDay,
                            int minuteOfHour) {
        super(context, theme);

        mCallBack = callBack;
        mCalendar = Calendar.getInstance();

        Context themeContext = getContext();
        setButton(BUTTON_POSITIVE, themeContext.getText(R.string.date_time_done), this);
        setIcon(0);

        LayoutInflater inflater =
                (LayoutInflater) themeContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.date_time_picker_dialog, null);
        setView(view);
        mPicker = (YmdhmPicker) view.findViewById(R.id.datetimePicker);
        mPicker.init(year, monthOfYear, dayOfMonth,hourOfDay, minuteOfHour, this);
        mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BLOCK_DESCENDANTS);
        updateTitle(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
    }

    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDateSet();
    }

    /**
     * Gets the {@link DatePicker} contained in this dialog.
     *
     * @return The calendar view.
     */
    public YmdhmPicker getPicker() {
        return mPicker;
    }

    /**
     * Sets the current date.
     *
     * @param year The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth The date day of month.
     */
    public void updateDate(int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        mPicker.updateDate(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
    }

    /**
     * set min date
     * @param minDate
     * @return
     */
    public DateTimePickerDialog setMinDate(long minDate){
        mPicker.setMinDate(minDate);
        return this;
    }

    /**
     * set max date
     * @param maxDate
     * @return
     */
    public DateTimePickerDialog setMaxDate(long maxDate){
        mPicker.setMaxDate(maxDate);
        return this;
    }

    /**
     * set where this picker is able to edit.Default is false
     * @param editable
     * @return
     */
    public DateTimePickerDialog setEditable(boolean editable){
        if (editable){
            mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BEFORE_DESCENDANTS);
        }else {
            mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BLOCK_DESCENDANTS);
        }

        return this;
    }

    public DateTimePickerDialog setUseHour(boolean useHour){
        mPicker.setUseHour(useHour);
        if (!useHour){
            setUseMinute(false);
        }else {
            updateWithDialog();
        }
        return this;
    }

    public DateTimePickerDialog setUseMinute(boolean useMinute){
        mPicker.setUseMinute(useMinute);
        if (useMinute){
            setUseHour(true);
        }else {
            updateWithDialog();
        }
        return this;
    }

    @Override
    public void setCustomTitle(View customTitleView) {
        super.setCustomTitle(customTitleView);
        updateTitle = false;
    }

    public DateTimePickerDialog setSelectionDivider(Drawable selectionDivider) {
        mPicker.setSelectionDivider(selectionDivider);
        return this;
    }

    public DateTimePickerDialog setSelectionDividerHeight(int selectionDividerHeight) {
        mPicker.setSelectionDividerHeight(selectionDividerHeight);
        return this;
    }

    /**
     * update the last data with Dialog
     */
    protected void updateWithDialog(){
        mPicker.updateSpinners();
        updateTitle(mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH),
                mCalendar.get(Calendar.DAY_OF_MONTH),
                mCalendar.get(Calendar.HOUR_OF_DAY),
                mCalendar.get(Calendar.MINUTE));
    }

    private void tryNotifyDateSet() {
        if (mCallBack != null) {
            mPicker.clearFocus();
            mCallBack.onDateSet(mPicker, mPicker.getYear(),
                    mPicker.getMonth(), mPicker.getDayOfMonth(), mPicker.getHourOfDay(), mPicker.getMinuteOfHour());
        }
    }

    private static String ForMat = "yyyy年M月d日";
    private boolean updateTitle = true;
    private void updateTitle(int year, int month, int day, int hour, int minute) {
        mCalendar.set(year, month, day, hour, minute);
        if (updateTitle) {
            StringBuilder sb = new StringBuilder(ForMat);
            if (mPicker.isUseHour()) {
                sb.append("h时");
            }
            if (mPicker.isUseMinute()) {
                sb.append("m分");
            }
            DateFormat df = new SimpleDateFormat(sb.toString());
            String title = df.format(mCalendar.getTimeInMillis());
            setTitle(title);
        }
    }

    @Override
    public Bundle onSaveInstanceState() {
        Bundle state = super.onSaveInstanceState();
        state.putInt(YEAR, mPicker.getYear());
        state.putInt(MONTH, mPicker.getMonth());
        state.putInt(DAY, mPicker.getDayOfMonth());
        state.putInt(HOUR, mPicker.getHourOfDay());
        state.putInt(MINUTE, mPicker.getMinuteOfHour());
        return state;
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        int year = savedInstanceState.getInt(YEAR);
        int month = savedInstanceState.getInt(MONTH);
        int day = savedInstanceState.getInt(DAY);
        int hour = savedInstanceState.getInt(HOUR);
        int minute = savedInstanceState.getInt(MINUTE);
        mPicker.init(year, month, day, hour, minute, this);
    }

    @Override
    public void onDateChanged(YmdhmPicker view, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
        updateTitle(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
    }


}
