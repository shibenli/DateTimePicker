package com.lany.picker.datetimepicker;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.lany.picker.R;
import com.lany.picker.datepicker.DatePicker;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Administrator on 2016/1/14.
 */
public class BLDateTimePicker extends DialogFragment implements View.OnClickListener,
        YmdhmPicker.OnDateChangedListener {

    private static final String YEAR = "year";
    private static final String MONTH = "month";
    private static final String DAY = "day";
    private static final String HOUR = "hour";
    private static final String MINUTE = "minute";

    /**
     * datetimepicker
     */
    private YmdhmPicker mPicker;

    /**
     * title View
     */
    private TextView mTitle;

    /**
     * title line
     */
    private View mTitleSp;

    /**
     * buttom line
     */
    private View mButtomSP;

    private OnDateSetListener mCallBack;
    private Calendar mCalendar;

    protected View mainView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        if (mainView == null){
            mainView = inflater.inflate(R.layout.date_time_picker_fragment, null);
            mPicker = (YmdhmPicker) mainView.findViewById(R.id.datetimePicker);
            mTitle = (TextView) mainView.findViewById(R.id.comsumer_title);
            mainView.findViewById(R.id.btn_date_time_1).setOnClickListener(this);
        }else {
            if (mainView.getParent() != null) {
                ((ViewGroup) mainView.getParent()).removeView(mainView);
            }
        }

        mCalendar = Calendar.getInstance();

        initView(savedInstanceState);
        return mainView;
    }

    /**
     * init
     */
    protected BLDateTimePicker initView(Bundle state) {
        if (state == null) {//first
            Bundle bundle = getArguments();
            Date init = (Date) bundle.getSerializable("Date");
            if (init == null) {
                init = new Date();
            }
            Calendar initDate = Calendar.getInstance();
            initDate.setTime(init);

            mPicker.setUseHour(useHour);
            mPicker.setUseMinute(useMinute);

            initView(initDate.get(Calendar.YEAR),
                    initDate.get(Calendar.MONTH),
                    initDate.get(Calendar.DAY_OF_MONTH),
                    initDate.get(Calendar.HOUR_OF_DAY),
                    initDate.get(Calendar.MINUTE));


        }else {//restore
            onRestoreInstanceState(state);
        }

        return  this;
    }

    /**
     * @param year The initial year of the dialog.
     * @param monthOfYear The initial month of the dialog.
     * @param dayOfMonth The initial day of the dialog.
     */
    protected BLDateTimePicker initView(
                            int year,
                            int monthOfYear,
                            int dayOfMonth,
                            int hourOfDay,
                            int minuteOfHour) {
        mPicker.init(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, this);
        mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BLOCK_DESCENDANTS);
        updateTitle(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour);
        return this;
    }

    public void onClick(View view) {
        tryNotifyDateSet();
        dismissAllowingStateLoss();
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
    public BLDateTimePicker setMinDate(long minDate){
        mPicker.setMinDate(minDate);
        return this;
    }

    /**
     * set max date
     * @param maxDate
     * @return
     */
    public BLDateTimePicker setMaxDate(long maxDate){
        mPicker.setMaxDate(maxDate);
        return this;
    }

    /**
     * set where this picker is able to edit.Default is false
     * @param editable
     * @return
     */
    public BLDateTimePicker setEditable(boolean editable){
        if (editable){
            mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BEFORE_DESCENDANTS);
        }else {
            mPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BLOCK_DESCENDANTS);
        }

        return this;
    }

    private boolean useHour;
    public BLDateTimePicker setUseHour(boolean useHour){
        this.useHour = useHour;
        if (!useHour){
            setUseMinute(false);
        }

        return this;
    }

    private boolean useMinute;
    public BLDateTimePicker setUseMinute(boolean useMinute){
        this.useMinute = useMinute;
        if (useMinute){
            setUseHour(true);
        }

        return this;
    }

    public BLDateTimePicker setSelectionDivider(Drawable selectionDivider) {
        mPicker.setSelectionDivider(selectionDivider);
        return this;
    }

    public BLDateTimePicker setSelectionDividerHeight(int selectionDividerHeight) {
        mPicker.setSelectionDividerHeight(selectionDividerHeight);
        return this;
    }

    /**
     * set datachanged callback
     * @param callBack
     * @return
     */
    public BLDateTimePicker setOnDateSetListener(OnDateSetListener callBack){
        this.mCallBack = callBack;
        return this;
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
            mTitle.setText(title);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putInt(YEAR, mPicker.getYear());
        savedInstanceState.putInt(MONTH, mPicker.getMonth());
        savedInstanceState.putInt(DAY, mPicker.getDayOfMonth());
        savedInstanceState.putInt(HOUR, mPicker.getHourOfDay());
        savedInstanceState.putInt(MINUTE, mPicker.getMinuteOfHour());
    }


    public void onRestoreInstanceState(Bundle savedInstanceState) {
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
