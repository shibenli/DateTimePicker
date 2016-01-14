package com.lany.picker.datetimepicker;

/**
 * Created by Administrator on 2016/1/14.
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
