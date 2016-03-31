package com.lany.picker.datapicker;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;

import com.lany.picker.R;
import com.lany.picker.numberpicker.NumberPicker;

import java.util.Arrays;

/**
 * Created by shibenli on 2016/3/31.
 */
public class DataPickerDialog extends AlertDialog implements DialogInterface.OnClickListener, NumberPicker.OnValueChangeListener {

    public interface OnDataSetListener {
        void onDataSet(NumberPicker view, int index);
    }

    private OnDataSetListener mCallback;

    protected NumberPicker mDataPicker;

    protected int index;

    public DataPickerDialog(Context context, OnDataSetListener callBack) {
        this(context,
                Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? R.style.Theme_Dialog_Alert
                        : 0, callBack);
    }

    protected DataPickerDialog(Context context, int theme, OnDataSetListener callback) {
        super(context, theme);

        Context themeContext = getContext();
        mCallback = callback;
        setButton(BUTTON_POSITIVE,
                themeContext.getText(R.string.date_time_done), this);

        LayoutInflater inflater = (LayoutInflater) themeContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.data_picker_dialog, null);
        setView(view);
        mDataPicker = (NumberPicker) view.findViewById(R.id.data_picker);
        mDataPicker.setOnValueChangedListener(this);
        updateTitle();
    }

    private void updateTitle() {
        String[] displayedValues = mDataPicker.getDisplayedValues();
        setTitle(displayedValues == null || displayedValues.length <= index ? "请选择" : displayedValues[index]);
    }

    public DataPickerDialog setDisplayedValues(String[] displayedValues) {
        index = mDataPicker.getValue();
        mDataPicker.setDisplayedValues(displayedValues);
        mDataPicker.setMaxValue(displayedValues.length - 1);
        mDataPicker.setMinValue(0);
        mDataPicker.setValue(index);
        mDataPicker.setWrapSelectorWheel(false);
        updateTitle();
        return this;
    }

    public DataPickerDialog setDividerDrawable(Drawable divider) {
        mDataPicker.setDividerDrawable(divider);
        return this;
    }


    public DataPickerDialog setSelectionDivider(Drawable selectionDivider) {
        this.mDataPicker.setSelectionDivider(selectionDivider);
        return this;
    }

    public DataPickerDialog setSelectionDividerHeight(int selectionDividerHeight) {
        this.mDataPicker.setSelectionDividerHeight(selectionDividerHeight);
        return this;
    }

    public DataPickerDialog upData(int index) {
        mDataPicker.setValue(index);
        updateTitle();
        return this;
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        index = newVal;
        updateTitle();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        tryNotifyDataSet();
    }

    private void tryNotifyDataSet() {
        if (mCallback != null) {
            mDataPicker.clearFocus();
            mCallback.onDataSet(mDataPicker, mDataPicker.getValue());
        }
    }
}
