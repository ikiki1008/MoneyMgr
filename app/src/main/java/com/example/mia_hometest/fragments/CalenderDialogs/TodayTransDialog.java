package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.fragments.card.SortCardListService;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class TodayTransDialog extends DialogFragment implements View.OnClickListener {

    private final String TAG = TodayTransDialog.class.getSimpleName();
    private Context mContext;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private TextView mIncome;
    private TextView mOutcome;
    private FrameLayout mCheckIncome;
    private FrameLayout mCheckOutcome;
    private CardListAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SortCardListService mService = null;
    private boolean isOutcomeVisible = false;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SortCardListService.LocalBinder binder = (SortCardListService.LocalBinder) iBinder;
            mService = binder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
        }
    };

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
        String formatDate = dateString.replace("-","/");
        String date = formatDate.substring(formatDate.lastIndexOf("/")+1);
        String yearMonth = dateString.substring(0, formatDate.lastIndexOf("/")); // 년/월 추출 (예: 2025-04)
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date)); // 요일 추출

        mDate = view.findViewById(R.id.selected_date);
        mYearMonth = view.findViewById(R.id.selected_month_year);
        mDay = view.findViewById(R.id.selected_day);
        mIncome = view.findViewById(R.id.income_price);
        mOutcome = view.findViewById(R.id.outcome_price);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        mCheckIncome = view.findViewById(R.id.incomeView);
        mCheckOutcome = view.findViewById(R.id.outcome_view);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mAdapter = new CardListAdapter(mContext);
        mRecyclerView.setAdapter(mAdapter);
        mDate.setText(date);
        mYearMonth.setText(yearMonth);
        mDay.setText(dayOfWeek);

        mCheckIncome.setOnClickListener(this);
        mCheckOutcome.setOnClickListener(this);

        return mBuilder.create();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
    
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Intent intent = new Intent(mContext, SortCardListService.class);
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop: ");
        mContext.unbindService(mConnection);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.incomeView:
                ImageView incomeArrow = view.findViewById(R.id.check_income);
                incomeArrow.setRotation(90);
                checkTodayMoney("income");
                break;
            case R.id.outcome_view:
                ImageView outcomeArrow = view.findViewById(R.id.check_outcome);
                outcomeArrow.setRotation(90);
                checkTodayMoney("outcome");
                /*if (!isOutcomeVisible) {
                    outcomeArrow.setRotation(90);
                    isOutcomeVisible = true;
                    checkTodayMoney("outcome");
                } else {
                    outcomeArrow.setRotation(0);
                  //  outcome_detail.setVisibility(View.GONE);
                    isOutcomeVisible = false;
                }*/
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

    private void checkTodayMoney(String value) {
        //오늘날짜에 저장된 목록을 가져온다
        List<ListItem> listItems = new ArrayList<>();

        String yearMonth = mYearMonth.getText().toString().replace("-","/");
        String today = yearMonth + "/" + mDate.getText().toString();
        Log.d(TAG, "checkTodayMoney: 오늘의 날짜는 " + today);

        if (value.equals("income")) {
            mService.getListString(today, mContext.getText(R.string.list_day).toString(), value);
            listItems = mService.getItems();
            Log.d(TAG, "checkTodayMoney: " + listItems);
            mAdapter.setItems(listItems);
        } else {
            mService.getListString(today, mContext.getText(R.string.list_day).toString(), value);
            listItems = mService.getItems(); // 지출 항목 가져오기
            Log.d(TAG, "checkTodayMoney 22 : " + listItems);
            mAdapter.setItems(listItems);
        }
    }
}
