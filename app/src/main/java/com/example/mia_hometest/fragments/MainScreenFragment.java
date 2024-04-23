package com.example.mia_hometest.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CalDialogView;
import com.example.mia_hometest.common.WeekdaysDecorator;
import com.example.mia_hometest.common.TestGridAdapter;
import com.example.mia_hometest.common.WeekendDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.util.Calendar;

public class MainScreenFragment extends Fragment {
    private final String TAG = MainScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;
    MaterialCalendarView mCal;
    private WeekdaysDecorator mDecorate;
    private WeekendDecorator mWeekendDaco;
    private CalDialogView mDialog;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.main_screen, container, false);

        mCal = view.findViewById(R.id.cal);
        mCal.setSelectedDate(CalendarDay.today());
        mCal.state().edit()
                .setFirstDayOfWeek(DayOfWeek.of(Calendar.SATURDAY))
                .setMinimumDate(CalendarDay.from(1930, 1, 1))
                .setMaximumDate(CalendarDay.from(2060, 12, 31))
                .setCalendarDisplayMode(CalendarMode.MONTHS)
                .commit();

        mDecorate = new WeekdaysDecorator(mContext);
        mWeekendDaco = new WeekendDecorator(mContext);
        mDialog = new CalDialogView(mContext);

        mCal.addDecorator(mWeekendDaco);
        mCal.addDecorator(mDecorate);
        mCal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    Bundle args = new Bundle();
                    args.putParcelable("selectedDay", date);
                    mDialog.setArguments(args);
                    assert getFragmentManager() != null;
                    mDialog.show(getFragmentManager(), "tag");

                } else {
                    Log.d(TAG, " 메인 스크린 화면에서 다이얼로그뷰를 끈다");
                    mDialog.dismiss();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " MainScreenFragment 끌게요");
        super.onDestroy();
    }

    public MainScreenFragment (Context context) {
        mContext = context;
    }

}
