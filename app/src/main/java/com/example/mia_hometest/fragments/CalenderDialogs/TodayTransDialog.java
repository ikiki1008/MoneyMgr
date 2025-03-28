package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.fragments.card.SortCardListViewModel;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.Calendar;

public class TodayTransDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = TodayTransDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private TextView mAddNewItem;
    private CalendarDay mToday;
    private CardListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SortCardListViewModel mViewModel;
    private boolean mNewItemPage = false;
    private final String CHECK = "android.intent.action.CHECK_NEW_ITEM";
    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CHECK)) {
                Log.d(TAG, "onReceive: 리시버 받았다!!!!");
                observeDate();
            }
        }
    };

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
        mAddNewItem = view.findViewById(R.id.addBtn);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CardListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mToday = getArguments().getParcelable("selectedDay");
        setupDateViews(mToday);
        mAddNewItem.setOnClickListener(this);

        String yearMonth = mYearMonth.getText().toString().replace("-","/");
        String today = yearMonth + "/" + mDate.getText().toString();
        Log.d(TAG, "checkTodayMoney: 오늘의 날짜는 " + today);
        mViewModel.getListString(today, mContext.getText(R.string.list_day).toString(), "all", mContext);
        LocalBroadcastManager.getInstance(mContext).registerReceiver(mReceiver, new IntentFilter(CHECK));

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(mContext).unregisterReceiver(mReceiver);
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

    @Override
    public void onClick(View view) {
        CalDialogView calDialogView = new CalDialogView(mContext);
        if (view.getId() == R.id.addBtn) {
           //다른 프래그먼트 불러오기
            Log.d(TAG, "onClick: clickclick");
            Bundle args = new Bundle();
            args.putParcelable("selectedDay", mToday);
            calDialogView.setArguments(args);
            calDialogView.show(getParentFragmentManager(), "selectedDay");
            mNewItemPage = true;
        } else {
            if (mNewItemPage) {
               calDialogView.dismiss();
               mNewItemPage = false;
            }
        }

    }
}
