package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
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
    private TextView mIncome;
    private TextView mOutcome;
    private FrameLayout mCheckIncome;
    private FrameLayout mCheckOutcome;
    private FrameLayout income_detail;
    private FrameLayout outcome_detail;
    private boolean isIncomeVisible = false;
    private boolean isOutcomeVisible = false;

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
        mIncome = view.findViewById(R.id.income_price);
        mOutcome = view.findViewById(R.id.outcome_price);
        income_detail = view.findViewById(R.id.income_detail);
        outcome_detail = view.findViewById(R.id.outcome_detail);
        mCheckIncome = view.findViewById(R.id.incomeView);
        mCheckOutcome = view.findViewById(R.id.outcome_view);

        income_detail.setVisibility(View.GONE);
        outcome_detail.setVisibility(View.GONE);

        mDate.setText(date);
        mYearMonth.setText(yearMonth);
        mDay.setText(dayOfWeek);

        mCheckIncome.setOnClickListener(this);
        mCheckOutcome.setOnClickListener(this);

        return mBuilder.create();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.incomeView:
                ImageView incomeArrow = view.findViewById(R.id.check_income);
                if (!isIncomeVisible) {
                    Log.d(TAG, "onClick: wtf");
                    incomeArrow.setRotation(90);
                    income_detail.setVisibility(View.VISIBLE);
                    isIncomeVisible = true;
                } else {
                    incomeArrow.setRotation(0);
                    income_detail.setVisibility(View.GONE);
                    isOutcomeVisible = false;
                }
                break;
            case R.id.outcome_view:
                ImageView outcomeArrow = view.findViewById(R.id.check_outcome);
                if (!isOutcomeVisible) {
                    Log.d(TAG, "onClick: wtfffffff");
                    outcomeArrow.setRotation(90);
                    outcome_detail.setVisibility(View.VISIBLE);
                    isOutcomeVisible = true;
                } else {
                    outcomeArrow.setRotation(0);
                    outcome_detail.setVisibility(View.GONE);
                    isOutcomeVisible = false;
                }
                break;
            default:
                break;
        }
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    private void checkTodayMoney() {
        //오늘날짜에 저장된 목록을 가져온다
    }
}
