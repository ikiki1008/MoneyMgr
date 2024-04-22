package com.example.mia_hometest.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CalDialogView;
import com.example.mia_hometest.common.CalenderDecorator;
import com.example.mia_hometest.common.TestGridAdapter;
import com.example.mia_hometest.common.WeekendDecorator;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.util.Calendar;
import java.util.List;

public class MainScreenFragment extends Fragment {
    private final String TAG = MainScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;
    MaterialCalendarView mCal;
    private CalenderDecorator mDecorate;
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

        mDecorate = new CalenderDecorator(mContext);
        mWeekendDaco = new WeekendDecorator(mContext);
        mDialog = new CalDialogView(mContext);

        mCal.addDecorator(mWeekendDaco);
//        mCal.addDecorator(mDecorate);
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
