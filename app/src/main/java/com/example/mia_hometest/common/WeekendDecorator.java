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

import java.util.Calendar;

public class WeekendDecorator implements DayViewDecorator {
    private Context mContext = null;

    @SuppressLint("UseCompatLoadingForDrawables")
    public WeekendDecorator(Context context) {
        mContext = context;
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        DayOfWeek dayOfWeek = DayOfWeek.of(day.getDate().getDayOfWeek().getValue());
        // 토요일 또는 일요일인 경우에만 true 반환
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.addSpan(new ForegroundColorSpan(Color.parseColor("#FF8B82")));
    }
}
