package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.text.style.ForegroundColorSpan;
import android.text.style.LineBackgroundSpan;

import androidx.annotation.NonNull;

import com.example.mia_hometest.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;

import java.util.Calendar;

public class CalenderDecorator implements DayViewDecorator {

    private final Drawable mBack;
    private Context mContext = null;

    @SuppressLint("UseCompatLoadingForDrawables")
    public CalenderDecorator (Context context) {
        mContext = context;
        mBack = mContext.getDrawable(R.drawable.cal_back);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return true;
    }

    @Override
    public void decorate(DayViewFacade view) {
        view.setSelectionDrawable(mBack);
    }
}
