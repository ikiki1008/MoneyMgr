package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;

import com.example.mia_hometest.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import org.threeten.bp.DayOfWeek;

public class WeekdaysDecorator implements DayViewDecorator {

    private Context mContext = null;

    @SuppressLint("UseCompatLoadingForDrawables")
    public WeekdaysDecorator(Context context) {
        mContext = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        DayOfWeek dayOfWeek = DayOfWeek.of(day.getDate().getDayOfWeek().getValue());

        //주중 색상 변경
        return dayOfWeek == DayOfWeek.MONDAY || dayOfWeek == DayOfWeek.TUESDAY ||
                dayOfWeek == DayOfWeek.WEDNESDAY || dayOfWeek == DayOfWeek.THURSDAY ||
                dayOfWeek == DayOfWeek.FRIDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#707070")));
    }
}
