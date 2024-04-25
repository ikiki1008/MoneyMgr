package com.example.mia_hometest.fragments.CalenderDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.TestGridAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class CalDialogView extends DialogFragment implements View.OnClickListener, CalCulListener {
    private final String TAG = CalDialogView.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;
    private ImageView mLeftCheck;
    private ImageView mRightCheck;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private TextView mLine1;
    private TextView mLine2;
    private TextView mLine3;
    private TextView mLine4;
    private TextView mLine5;

    private CalculatorDialogView mCalDialog;
    private CateDialogView mCategory;

    public CalDialogView (Context context) {
        mContext = context;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        CalendarDay day = getArguments().getParcelable("selectedDay");
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.cal_dialog_screen, null);

        initViews(view);

        String dateString = day.getDate().toString();
        String date = dateString.substring(dateString.lastIndexOf("-") + 1); // 날짜 추출 (예: 21)
        String yearMonth = dateString.substring(0, dateString.lastIndexOf("-")); // 년/월 추출 (예: 2025-04)
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date)); // 요일 추출

        mDate.setText(date);
        mYearMonth.setText(yearMonth.replace("-", "/"));
        mDay.setText(dayOfWeek);
        mLeftCheck.setImageResource(R.drawable.check);
        mLine1.setText(dateString.replace("-", "/")); //다이얼로그 선택 시 해당 날짜 추가

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);
        return mBuilder.create();
    }

    private void initViews(View view) {
        int[] textViews= {
                R.id.selected_date, R.id.selected_month_year, R.id.selected_day,
                R.id.income, R.id.outcome, R.id.cancelBtn, R.id.editBtn,
                R.id.line1, R.id.line2, R.id.line3, R.id.line4, R.id.line5
        };

        int[] imagesViews = {
                R.id.leftCheck, R.id.rightCheck
        };

        for (int id : textViews) {
            TextView textView = view.findViewById(id);
            textView.setOnClickListener(this);
        }

        for (int image : imagesViews) {
            ImageView imageView = view.findViewById(image);
            imageView.setOnClickListener(this);
        }

        mDate = view.findViewById(textViews[0]);
        mYearMonth = view.findViewById(textViews[1]);
        mDay = view.findViewById(textViews[2]);

        mLine1 = view.findViewById(textViews[7]);
        mLine2 = view.findViewById(textViews[8]);
        mLine3 = view.findViewById(textViews[9]);
        mLine4 = view.findViewById(textViews[10]);
        mLine5 = view.findViewById(textViews[11]);

        mLeftCheck = view.findViewById(imagesViews[0]);
        mRightCheck = view.findViewById(imagesViews[1]);
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.editBtn:
                Log.d(TAG, "onClick: 버튼 클릭클릭클릭");
                break;
            case R.id.cancelBtn:
                if (getDialog() != null && getDialog().isShowing()) {
                    dismiss();
                }
                break;
            case R.id.income:
                mLeftCheck.setImageResource(R.drawable.check);
                mRightCheck.setImageResource(0);
                break;
            case R.id.outcome:
                mRightCheck.setImageResource(R.drawable.check);
                mLeftCheck.setImageResource(0);
                break;
            case R.id.line2:
                mCalDialog = new CalculatorDialogView(mContext, this);
                mCalDialog.show(getChildFragmentManager(), "child_fragment");
                break;
            case R.id.line3:
                mCategory = new CateDialogView(mContext, this);
                mCategory.show(getChildFragmentManager(), "child_category");
                break;
            case R.id.line4:
                break;
            default:
                break;
        }
    }

    @Override
    public void onClicked(Intent intent, String type) {
        switch (type) {
            case "calculator" :
                int value = intent.getIntExtra("value", 0);
                String result = String.valueOf(value);
                mLine2.setText(result);
                break;
            default:
                break;
        }
    }
}