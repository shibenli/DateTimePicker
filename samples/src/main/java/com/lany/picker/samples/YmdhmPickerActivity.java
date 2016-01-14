package com.lany.picker.samples;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.lany.picker.datetimepicker.BLDateTimePicker;
import com.lany.picker.datetimepicker.DateTimePickerDialog;
import com.lany.picker.datetimepicker.OnDateSetListener;
import com.lany.picker.datetimepicker.YmdhmPicker;

import java.util.Calendar;
import java.util.Date;

public class YmdhmPickerActivity extends AppCompatActivity {
    private TextView showText;

    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ymdhm_picker);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        showText = (TextView) findViewById(R.id.lany_picker_show_text);

        findViewById(R.id.launch_dialog).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DateTimePickerDialog(YmdhmPickerActivity.this,
                        callBack, Calendar.getInstance()).setUseMinute(true).show();
            }
        });

        findViewById(R.id.launch_fragment).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("Date", new Date());
                BLDateTimePicker picker = new BLDateTimePicker().setUseMinute(true).setOnDateSetListener(callBack);
                picker.setArguments(bundle);
                picker.show(getFragmentManager(),"DateTimePicker");

            }
        });

        YmdhmPicker ymdhPicker = (YmdhmPicker) findViewById(R.id.ymdhPicker);
        ymdhPicker.setSelectionDivider(new ColorDrawable(0xff000000));
        ymdhPicker.setSelectionDividerHeight(2);
        ymdhPicker.setDescendantFocusability(YmdhmPicker.FOCUS_BLOCK_DESCENDANTS);
        ymdhPicker.setMaxDate(new Date().getTime());
        ymdhPicker.setMinDate(new Date().getTime()-1000*60*60*24*365);
        ymdhPicker.setOnDateChangedListener(new YmdhmPicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(YmdhmPicker view, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
                mYear = year;
                mMonth = monthOfYear;
                mDay = dayOfMonth;
                mHour = hourOfDay;
                mMinute = minuteOfHour;
                updateDisplay();
            }
        });
    }

    private void updateDisplay() {
        showText.setText(new StringBuilder()
                .append(mYear).append("年")
                .append(mMonth).append("月")
                .append(mDay).append("日")
                .append(mHour).append("时")
                .append(mMinute).append("分"));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private OnDateSetListener callBack = new OnDateSetListener() {
        @Override
        public void onDateSet(YmdhmPicker view, int year, int monthOfYear, int dayOfMonth, int hourOfDay, int minuteOfHour) {
            mYear = year;
            mMonth = monthOfYear;
            mDay = dayOfMonth;
            mHour = hourOfDay;
            mMinute = minuteOfHour;
            updateDisplay();
        }
    };
}
