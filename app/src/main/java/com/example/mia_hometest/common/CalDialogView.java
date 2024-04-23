package com.example.mia_hometest.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class CalDialogView extends DialogFragment implements View.OnClickListener{
    private final String TAG = CalDialogView.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private ImageView mBtn;

    public CalDialogView (Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        CalendarDay day = getArguments().getParcelable("selectedDay");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.cal_dialog_screen, null);

        mDate = view.findViewById(R.id.selected_date);
        mYearMonth = view.findViewById(R.id.selected_month_year);
        mDay = view.findViewById(R.id.selected_day);
        mBtn = view.findViewById(R.id.editBtn);
        mBtn.setOnClickListener(this);

        mAdapter = new TestGridAdapter(mContext);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        String dateString = day.getDate().toString();
        String date = dateString.substring(dateString.lastIndexOf("-") + 1); // 날짜 추출 (예: 21)
        String yearMonth = dateString.substring(0, dateString.lastIndexOf("-")); // 년/월 추출 (예: 2025-04)
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date)); // 요일 추출

        mDate.setText(date);
        mYearMonth.setText(yearMonth.replace("-", "/"));
        mDay.setText(dayOfWeek);

        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setView(view);
        builder.setCancelable(true);
        return builder.create();
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.editBtn) {
            Log.d(TAG, "onClick: 버튼 클릭클릭클릭");
        } else {
            Log.d(TAG, "onClick: 아무것도 안함");
        }
    }
}