package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.mia_hometest.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class TodayTransDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = TodayTransDialog.class.getSimpleName();
    private Context mContext = null;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;

    public TodayTransDialog (Context context) {
        mContext = context;
    }

    public android.app.Dialog onCreateDialog (Bundle savedInstance) {
        CalendarDay day = getArguments().getParcelable("selectedDay");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.today_trans_item_list, null);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        String dateString = day.getDate().toString();
        String date = dateString.substring(dateString.lastIndexOf("-")+1);
        String yearMonth = dateString.substring(0, dateString.lastIndexOf("-")); // 년/월 추출 (예: 2025-04)
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date)); // 요일 추출

        mDate = view.findViewById(R.id.selected_date);
        mYearMonth = view.findViewById(R.id.selected_month_year);
        mDay = view.findViewById(R.id.selected_day);

        mDate.setText(date);
        mYearMonth.setText(yearMonth);
        mDay.setText(dayOfWeek);

        return mBuilder.create();
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    @Override
    public void onClick(View view) {

    }
}
