package com.example.mia_hometest.fragments.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.common.SpecificListItem;
import com.example.mia_hometest.fragments.card.CardScreenInfoFragment;
import com.example.mia_hometest.fragments.card.SortCardListService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class CardScreenFragment extends Fragment implements View.OnClickListener, CardListAdapter.OnSwipeListener, CardListAdapter.OnListClickListener {
    private final String TAG = CardScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private TextView mAll = null;
    private TextView mExpense = null;
    private TextView mIncome = null;
    private TextView mListTitle = null;
    private ImageView mBtn = null;
    private ImageView mLeftArrow = null;
    private ImageView mRightArrow= null;
    private TextView mArrayDate = null;
    private String[] mlist;
    private String mDate;
    private String mAmount;
    private String mCate;
    private String mAcc;
    private String mNote;
    private String mTrans;
    private Drawable mImage;
    private AlertDialog.Builder mBuilder;
    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;
    private FirebaseFirestore mStore;
    private CardScreenInfoFragment mCardScreenInfoFragment = null;
    private AlertDialog mAlertDialog;
    private SortCardListService mService;
    private boolean mIsServiceOn = false;
    private boolean mIsOutcome = false;
    private boolean mIsAll = true;
    private Calendar mCalender = Calendar.getInstance();
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SortCardListService.LocalBinder binder = (SortCardListService.LocalBinder) iBinder;
            mService = binder.getService();
            mIsServiceOn = true;
            if (!mListTitle.getText().equals("") && mIsAll) {
                mIsOutcome = false;
                mService.getListString(mListTitle.getText().toString(), "All");
                List<ListItem> listItems = mService.getItems();
                mAdapter.setItems(listItems);
                Log.d(TAG, "onServiceConnected: 리스트 아이템들은 ... " + listItems);
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mIsServiceOn = false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " CardScreenFragment onCreateView: ");
        View view = inflater.inflate(R.layout.expense_all_screen, container, false);

        mStore = FirebaseFirestore.getInstance();
        mAll = view.findViewById(R.id.all);
        mExpense = view.findViewById(R.id.expense);
        mIncome = view.findViewById(R.id.income);
        mBtn = view.findViewById(R.id.listBtn);
        mListTitle = view.findViewById(R.id.listText);
        mLeftArrow = view.findViewById(R.id.arrow_left);
        mRightArrow = view.findViewById(R.id.arrow_right);
        mArrayDate = view.findViewById(R.id.array_dates);

        mAdapter = new CardListAdapter(mContext);
        mAdapter.setSwipeListener(this);
        mAdapter.setListClickListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mAll.setOnClickListener(this);
        mIncome.setOnClickListener(this);
        mExpense.setOnClickListener(this);
        mBtn.setOnClickListener(this);
        mLeftArrow.setOnClickListener(this);
        mRightArrow.setOnClickListener(this);

        mListTitle.setText(R.string.list_week);
        int themeColor = getThemeColor(android.R.attr.textColor);
        mAll.setTextColor(themeColor);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Intent intent = new Intent(mContext, SortCardListService.class); // service class connect!
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());
        String currentDate = dateFormat.format(Calendar.getInstance().getTime());
        mArrayDate.setText(currentDate); // 날짜 텍스트 설정
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mIsServiceOn) {
            Log.d(TAG, "onPause: 서비스 끄기``");
            mContext.unbindService(mConnection);
            mIsServiceOn = false;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " CardScreenFragment 끌게요");
        super.onDestroy();
    }

    public CardScreenFragment (Context context) {
        mContext = context;
    }

    public interface OnListItemClick {
        void onItemClick (String item);
    }

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        if (mContext.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        } else {
            throw new IllegalArgumentException("Attribute not found in theme");
        }
    }

    private void showList(final OnListItemClick listener) {
        mlist = getResources().getStringArray(R.array.list_dialog);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setItems(mlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String item = mlist[i];
                listener.onItemClick(item);
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    /*
     * CardScreenInfoFragment 의 인스턴스를 두개로 나눈다. income 이냐 expense 냐에 따라 카테고리가 인자로 쓰일지 결정되는 부분이기 떄문이다.
     * */
    private void startSpecificFragment(String transactions, String docId) {
        if (transactions.equals(mContext.getString(R.string.income))) {
            CardScreenInfoFragment fragment = CardScreenInfoFragment.newInstance(mTrans, docId, mDate, mAmount, mAcc, mNote);
            ((UserMainActivity) getActivity()).launchFragment(fragment);
        } else if (transactions.equals(mContext.getString(R.string.expense))){
            CardScreenInfoFragment fragment = CardScreenInfoFragment.newInstance(mTrans, docId, mDate, mAmount, mCate, mAcc, mNote);
            ((UserMainActivity) getActivity()).launchFragment(fragment);
        }
    }

    private void showWarning (int position) {
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(mContext.getString(R.string.warn_list));
        mBuilder.setPositiveButton(mContext.getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: 해당 정보 삭제합니다");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    ListItem itemToDelete = mAdapter.getItems().get(position);

                    Task<Void> deleteIncomeTask = mStore.collection("user").document(userId).collection("income").document(itemToDelete.getId())
                            .delete();

                    Task<Void> deleteOutcomeTask = mStore.collection("user").document(userId).collection("outcome").document(itemToDelete.getId())
                            .delete();

                    // 먼저 income에서 삭제 시도
                    deleteIncomeTask.addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully deleted from income!");
                        mAdapter.getItems().remove(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItems().size());
                    }).addOnFailureListener(e -> {
                        // income에서 실패하면 outcome에서 삭제 시도
                        Log.w(TAG, "Error deleting document from income, trying outcome", e);
                        deleteOutcomeTask.addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "DocumentSnapshot successfully deleted from outcome!");
                            mAdapter.getItems().remove(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItems().size());
                        }).addOnFailureListener(e2 -> Log.w(TAG, "Error deleting document from outcome", e2));
                    });
                }
            }
        });
        mBuilder.setNegativeButton(mContext.getString(R.string.answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
            }
        });
        mAlertDialog = mBuilder.create();
        mBuilder.show();
    }

    private void updateArrayText(String item) {
        //리스트를 클릭시 마다 달력 초기화 하기
        if (item.equals("연별") || item.equals("Yearly") ||
                item.equals("월별") || item.equals("Monthly") ||
                item.equals("주별") || item.equals("Weekly") ||
                item.equals("하루") || item.equals("Daily")) {
            mCalender = Calendar.getInstance();
        }

        switch (item) {
            case "연별":
            case "Yearly":
                mArrayDate.setText(getFormattedDate("yyyy", Calendar.getInstance())); // 현재 연도 텍스트 설정
                break;
            case "월별":
            case "Monthly":
                mArrayDate.setText(getFormattedDate("yyyy/MM", Calendar.getInstance())); // 월 텍스트 설정
                break;
            case "하루":
            case "주별":
            case "Daily":
            case "Weekly":
                mArrayDate.setText(getFormattedDate("yyyy/MM/dd", Calendar.getInstance())); // 날짜 텍스트 설정
                break;
            case "left":
                updateDate(-1); // 이전 날짜로 업데이트
                break;
            case "right":
                updateDate(1); // 다음 날짜로 업데이트
                break;
            default:
                break;
        }
    }

    private void updateDate(int increment) {
        // mListTitle의 텍스트에 따라 날짜 증감 처리
        switch (mListTitle.getText().toString()) {
            case "연별":
            case "list_year": // 연도 단위로 증감
                mCalender.add(Calendar.YEAR, increment);
                mArrayDate.setText(getFormattedDate("yyyy", mCalender));
                break;
            case "월별":
            case "list_month": // 월 단위로 증감
                mCalender.add(Calendar.MONTH, increment);
                mArrayDate.setText(getFormattedDate("yyyy/MM", mCalender));
                break;
            case "주별":
            case "list_week": // 주 단위로 증감
                mCalender.add(Calendar.WEEK_OF_YEAR, increment);
                mArrayDate.setText(getFormattedDate("yyyy/MM/dd", mCalender));
                break;
            case "하루":
            case "list_day": // 일 단위로 증감
                mCalender.add(Calendar.DAY_OF_YEAR, increment);
                mArrayDate.setText(getFormattedDate("yyyy/MM/dd", mCalender));
                break;
            default:
                break;
        }
    }

    // 날짜 형식에 따라 포맷된 문자열을 반환하는 메서드
    private String getFormattedDate(String format, Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(format, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    @Override
    public void onClick(View view) {
        int themeColor = getThemeColor(android.R.attr.textColor);
        int secondColor = getThemeColor(android.R.attr.textColorSecondary);
        List<ListItem> listItems = new ArrayList<>();

        switch (view.getId()) {
            case R.id.all:
                Log.d(TAG, "onClick: all");
                mIsAll = true;
                mIsOutcome = false;
                mAll.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "All");
                listItems = mService.getItems();
                mAdapter.setItems(listItems);
                break;
            case R.id.expense:
                Log.d(TAG, "onClick: expense");
                mIsAll = false;
                mIsOutcome = true;
                mExpense.setTextColor(themeColor);
                mAll.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "outcome");
                listItems = mService.getItems();
                mAdapter.setItems(listItems);
                break;
            case R.id.income:
                Log.d(TAG, "onClick: income");
                mIsAll = false;
                mIsOutcome = false;
                mIncome.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mAll.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "income");
                listItems = mService.getItems();
                mAdapter.setItems(listItems);
                break;
            case R.id.arrow_left:
                updateArrayText(mContext.getString(R.string.array_date_left));
                break;
            case R.id.arrow_right:
                updateArrayText(mContext.getString(R.string.array_date_right));
                break;
            case R.id.listBtn:
                showList(new OnListItemClick() {
                    @Override
                    public void onItemClick(String item) {
                        Log.d(TAG, "onItemClick: " + item);
                        if (item != null && !item.equals("")) {
                            mListTitle.setText(item);
                            updateArrayText(item);
                            /*if (mIsAll) {
                                mService.getListString(mListTitle.getText().toString(), "All");
                            } else if (!mIsAll && mIsOutcome) {
                                mService.getListString(mListTitle.getText().toString(), "outcome");
                            } else if (!mIsAll && !mIsOutcome) {
                                mService.getListString(mListTitle.getText().toString(), "income");
                            }*/
                        } else {
                            mAlertDialog.dismiss();
                        }
                    }
                });
                break;
            default:
                Log.d(TAG, "onClick: 랄라라라라 디폴트");
                break;
        }
    }

    @Override
    public void onSwipeLeft(int position) {
        Log.d(TAG, "onSwipeLeft: 왼쪽으로 스와이핑 했습니다.");
        showWarning(position);
    }

    @Override
    public void onListClick(int position) {
        Log.d(TAG, "onListClick: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            ListItem item = mAdapter.getItems().get(position);
            String itemId = item.getId();
            Log.d(TAG, "onSwipeLeft: 아이템 아이디는? " + itemId);

            mStore.collection("user").document(userId).collection("income").document(itemId).get()
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "onSwipeLeft: income onComplete");
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                mDate = documentSnapshot.getString("date");
                                mAmount = documentSnapshot.getString("amount");
                                mAcc = documentSnapshot.getString("account");
                                mNote = documentSnapshot.getString("note");
                                mTrans = mContext.getString(R.string.income);
                                if (mNote == null) {
                                    mNote = "No Description";
                                }
                                startSpecificFragment(mTrans, itemId);
                            } else {
                                Log.d(TAG, "onSwipeLeft: 인컴에 없음");
                                mStore.collection("user").document(userId).collection("outcome").document(itemId).get()
                                        .addOnCompleteListener(outcomeTask -> {
                                            Log.d(TAG, "onSwipeLeft: outcome onComplete");
                                            if (outcomeTask.isSuccessful()) {
                                                Log.d(TAG, "onSwipeLeft: outcome success");
                                                DocumentSnapshot documentSnapshot1 = outcomeTask.getResult();
                                                if (documentSnapshot1.exists()) {
                                                    mDate = documentSnapshot1.getString("date");
                                                    mAmount = documentSnapshot1.getString("amount");
                                                    mAcc = documentSnapshot1.getString("account");
                                                    mCate = documentSnapshot1.getString("category");
                                                    mNote = documentSnapshot1.getString("note");
                                                    mTrans = mContext.getString(R.string.expense);
                                                    if (mNote == null) {
                                                        mNote = "No Description";
                                                    }
                                                    Log.d(TAG, "onSwipeLeft: 도큐먼트가 있다면 ... " + mDate + mAmount + mAcc + mCate + mNote + mTrans);
                                                    startSpecificFragment(mTrans, itemId);
                                                } else {
                                                    Log.d(TAG, "onSwipeLeft: wtf111");
                                                }
                                            } else {
                                                Log.d(TAG, "onSwipeLeft: wtf 22222");
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}
