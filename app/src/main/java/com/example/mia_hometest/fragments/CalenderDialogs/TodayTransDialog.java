package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.fragments.card.SortCardListViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class TodayTransDialog extends DialogFragment {

    private final String TAG = TodayTransDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private CardListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SortCardListViewModel mViewModel;

    public TodayTransDialog (Context context) {
        mContext = context;
    }

    public android.app.Dialog onCreateDialog (Bundle savedInstance) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.today_trans_item_list, null);
        mViewModel = new ViewModelProvider(this).get(SortCardListViewModel.class);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        mDate = view.findViewById(R.id.selected_date);
        mYearMonth = view.findViewById(R.id.selected_month_year);
        mDay = view.findViewById(R.id.selected_day);
        mRecyclerView = view.findViewById(R.id.recyclerView);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CardListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        CalendarDay day = getArguments().getParcelable("selectedDay");
        setupDateViews(day);

        String yearMonth = mYearMonth.getText().toString().replace("-","/");
        String today = yearMonth + "/" + mDate.getText().toString();
        Log.d(TAG, "checkTodayMoney: 오늘의 날짜는 " + today);
        mViewModel.getListString(today, mContext.getText(R.string.list_day).toString(), "all", mContext);

        return mBuilder.create();
    }

    private void setupDateViews(CalendarDay day) {
        String dateString = day.getDate().toString();
        String formatDate = dateString.replace("-", "/");
        String date = formatDate.substring(formatDate.lastIndexOf("/") + 1);
        String yearMonth = dateString.substring(0, formatDate.lastIndexOf("/"));
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date));

        mDate.setText(date);
        mYearMonth.setText(yearMonth);
        mDay.setText(dayOfWeek);
    }

    @Override
    public void onResume() {
        super.onResume();
        observeDate();
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    private void observeDate() {
        mViewModel.getItems().observe(this, items -> {
            mAdapter.setItems(items);
        });
    }

}
