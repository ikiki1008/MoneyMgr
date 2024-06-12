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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.DialogItem;
import com.example.mia_hometest.common.DialogListAdapter;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.text.DateFormatSymbols;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalDialogView extends DialogFragment implements View.OnClickListener, CalCulListener, DialogListAdapter.OnItemClickListener {
    private final String TAG = CalDialogView.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private DialogListAdapter mAdapter;
    private CalculatorDialogView mCalDialog;
    private CateDialogView mCategory;
    private AccountDialogView mAccount;
    private NoteDialogView mNote;

    private ImageView mLeftCheck;
    private ImageView mRightCheck;
    private AlertDialog.Builder mBuilder;
    private TextView mDate;
    private TextView mYearMonth;
    private TextView mDay;
    private TextView mLine1;
    private List<DialogItem> mItemList = new ArrayList<>();

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

        mAdapter = new DialogListAdapter(mContext, this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        String dateString = day.getDate().toString();
        String date = dateString.substring(dateString.lastIndexOf("-") + 1); // 날짜 추출 (예: 21)
        String yearMonth = dateString.substring(0, dateString.lastIndexOf("-")); // 년/월 추출 (예: 2025-04)
        String dayOfWeek = getDayOfWeek(day.getYear(), day.getMonth(), Integer.parseInt(date)); // 요일 추출

        mDate.setText(date);
        mYearMonth.setText(yearMonth.replace("-", "/"));
        mDay.setText(dayOfWeek);

        setItems();
        Log.d(TAG, "onCreateDialog: set items 이후.......");

        mRightCheck.setImageResource(R.drawable.check); //set the Expense Screen first bc we already know it
        mLine1.setText(dateString.replace("-", "/")); //다이얼로그 선택 시 해당 날짜 추가

        return mBuilder.create();
    }

    private void initViews(View view) {
        int[] textViews= {
                R.id.selected_date, R.id.selected_month_year, R.id.selected_day,
                R.id.income, R.id.outcome, R.id.cancelBtn, R.id.editBtn,
                R.id.line1
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
        mLeftCheck = view.findViewById(imagesViews[0]);
        mRightCheck = view.findViewById(imagesViews[1]);
    }

    private void setItems() {
        mItemList.clear();
        String[] titles = getResources().getStringArray(R.array.outcome_dialog_title);
        String[] descs = new String[titles.length];

        for (int i=0; i<titles.length; i++) {
            DialogItem item = new DialogItem(titles[i], descs[i]);
            mItemList.add(item);
        }

        mAdapter.setItems(mItemList);
    }

    private String getDayOfWeek(int year, int month, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month - 1, day);
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        return new DateFormatSymbols().getShortWeekdays()[dayOfWeek];
    }

    @SuppressLint({"NonConstantResourceId", "NotifyDataSetChanged"})
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
                Log.d(TAG, "onClick: incomeeeeeeeeee");
                mLeftCheck.setImageResource(R.drawable.check);
                mRightCheck.setImageResource(0);
                mItemList.remove(1);
                mAdapter.notifyItemRemoved(1);
                break;
            case R.id.outcome:
                Log.d(TAG, "onClick:  outcomeeeeeeeeeeeeeee");
                mRightCheck.setImageResource(R.drawable.check);
                mLeftCheck.setImageResource(0);

                String[] outcomeTitles = getResources().getStringArray(R.array.outcome_dialog_title);
                String category = outcomeTitles[1];
                mItemList.add(1, new DialogItem(category, ""));
                mAdapter.notifyItemInserted(1);
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
                mItemList.get(0).setDesc(result);
                mAdapter.notifyItemChanged(0);
                break;
            case "category" :
                String cate = intent.getStringExtra("value");
                if (cate.equals(" ") || cate.isEmpty()) {
                    Log.d(TAG, "onClicked: 1");
                    mItemList.get(1).setDesc(null);
                } else {
                    Log.d(TAG, "onClicked: 2");
                    mItemList.get(1).setDesc(cate);
                }
                mAdapter.notifyItemChanged(1);
                break;
            case "account" :
                String acc = intent.getStringExtra("value");
                if (acc.equals(" ") || acc.isEmpty()) {
                    mItemList.get(2).setDesc(null);
                } else {
                    mItemList.get(2).setDesc(acc);
                }
                mAdapter.notifyItemChanged(2);
                break;
            case "note" :
                String note = intent.getStringExtra("value");
                if (note.equals(" ") || note.isEmpty()) {
                    mItemList.get(3).setDesc(null);
                } else {
                    mItemList.get(3).setDesc(note);
                }
                mAdapter.notifyItemChanged(3);
            default:
                break;
        }
    }

    @Override
    public void onItemClick(int position) {
        switch (position) {
            case 0 :
                mCalDialog = new CalculatorDialogView(mContext, this);
                mCalDialog.show(getChildFragmentManager(), "child_fragment");
                break;
            case 1 :
                mCategory = new CateDialogView(mContext, this);
                mCategory.show(getChildFragmentManager(), "child_category");
                break;
            case 2:
                mAccount = new AccountDialogView(mContext, this);
                mAccount.show(getChildFragmentManager(), "child_acc");
                break;
            case 3:
                mNote = new NoteDialogView(mContext, this);
                mNote.show(getChildFragmentManager(), "child_note");
                break;
            default:
                break;
        }
    }
}