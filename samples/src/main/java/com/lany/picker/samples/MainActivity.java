package com.lany.picker.samples;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.lany.picker.datapicker.DataPickerDialog;
import com.lany.picker.numberpicker.NumberPicker;

public class MainActivity extends AppCompatActivity {
    String[] data = new String[]{"小学","初中","高中","大学","硕士研究生","博士研究生"};
    private int current = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_layout);
        findViewById(R.id.datepicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, DatePickerActivity.class));
            }
        });
        findViewById(R.id.timepicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, TimePickerActivity.class));
            }
        });

        findViewById(R.id.lanypicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LanyPickerActivity.class));
            }
        });

        findViewById(R.id.calendar_view_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, CalendarViewActivity.class));
            }
        });
        findViewById(R.id.ymdhpicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, YmdhPickerActivity.class));
            }
        });
        findViewById(R.id.ymdhmpicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, YmdhmPickerActivity.class));
            }
        });
        findViewById(R.id.datapicker_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DataPickerDialog(MainActivity.this, new DataPickerDialog.OnDataSetListener() {
                    @Override
                    public void onDataSet(NumberPicker view, int index) {
                        Toast.makeText(view.getContext(), data[index], Toast.LENGTH_SHORT).show();
                    }
                }).setDisplayedValues(data).setSelectionDivider(new ColorDrawable(0xffff0000))
                .setSelectionDividerHeight(4).upData(current).show();
            }
        });
    }
}
