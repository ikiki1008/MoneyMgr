package com.example.mia_hometest.fragments.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.WeekdaysDecorator;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.common.WeekendDecorator;
import com.example.mia_hometest.fragments.CalenderDialogs.CalDialogView;
import com.example.mia_hometest.fragments.CalenderDialogs.TodayTransDialog;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import org.threeten.bp.DayOfWeek;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class MainScreenFragment extends Fragment {
    private final String TAG = MainScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;
    List<ListItem> mListItem = new ArrayList<>();
    MaterialCalendarView mCal;
    private WeekdaysDecorator mDecorate;
    private WeekendDecorator mWeekendDaco;
    private CalDialogView mDialog;
    private TodayTransDialog mTodayTransDialog;
    private AnyChartView mPieView;
    private Pie mPie;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
    }

    @SuppressLint("CutPasteId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        View view = inflater.inflate(R.layout.main_screen, container, false);

        mPieView = (AnyChartView) view.findViewById(R.id.pieChart);
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
        mTodayTransDialog = new TodayTransDialog(mContext);

        mCal.addDecorator(mWeekendDaco);
        mCal.addDecorator(mDecorate);
        mCal.setHeaderTextAppearance(R.style.CalenderHeader);

        Pie pie = AnyChart.pie();
        pie.labels().position("outside");

        List<DataEntry> data = new ArrayList<>();
        data.add(new ValueDataEntry("Apples", 6371664));
        data.add(new ValueDataEntry("Pears", 789622));
        data.add(new ValueDataEntry("Bananas", 7216301));
        data.add(new ValueDataEntry("Grapes", 1486621));
        data.add(new ValueDataEntry("Oranges", 1200000));
        pie.data(data);

        pie.legend().title().enabled(false);
        pie.legend().enabled(false);
        pie.background().fill("#252525");
        mPieView.setChart(pie);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");

        mCal.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                if (selected) {
                    Bundle args = new Bundle();
                    args.putParcelable("selectedDay", date);
                    mTodayTransDialog.setArguments(args);
                    assert getFragmentManager() != null;
                    mTodayTransDialog.show(getFragmentManager(), "tag");
                } else {
                    Log.d(TAG, " 메인 스크린 화면에서 다이얼로그뷰를 끈다");
                    //mDialog.dismiss();
                    mTodayTransDialog.dismiss();
                }
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, " MainScreenFragment 끌게요");
    }

    public MainScreenFragment (Context context) {
        mContext = context;
    }

}
