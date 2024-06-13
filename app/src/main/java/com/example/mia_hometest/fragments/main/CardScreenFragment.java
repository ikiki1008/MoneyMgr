package com.example.mia_hometest.fragments.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.CardListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardScreenFragment extends Fragment implements View.OnClickListener, CardListAdapter.OnSwipeListener{
    private final String TAG = CardScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private TextView mAll = null;
    private TextView mExpense = null;
    private TextView mIncome = null;
    private TextView mListTitle = null;
    private ImageView mBtn = null;
    private String[] mlist;
    private AlertDialog.Builder mBuilder;
    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;
    private FirebaseFirestore mStore;
    private boolean mLeftSwipe = false;

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

        mAdapter = new CardListAdapter(mContext);
        mAdapter.setSwipeListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mAll.setOnClickListener(this);
        mIncome.setOnClickListener(this);
        mExpense.setOnClickListener(this);
        mBtn.setOnClickListener(this);

        mListTitle.setText(R.string.list_week);
        int themeColor = getThemeColor(android.R.attr.textColor);
        mAll.setTextColor(themeColor);

        fetchData("all");
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " CardScreenFragment 끌게요");
        super.onDestroy();
    }

    public CardScreenFragment (Context context) {
        mContext = context;
    }

    public void fetchData(String type) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (type == "all") {
            if (user != null) {
                String userId = user.getUid();
                List<ListItem> listItems = new ArrayList<>();

                // Fetch income data
                mStore.collection("user").document(userId).collection("income")
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    String cate = "Income";
                                    String amount = documentSnapshot.getString("amount");
                                    String date = documentSnapshot.getString("date");
                                    amount = "+" + amount;
                                    listItems.add(new ListItem(cate, amount, date));
                                }

                                // After fetching all income data, fetch outcome data
                                mStore.collection("user").document(userId).collection("outcome")
                                        .get().addOnCompleteListener(outcomeTask -> {
                                            if (outcomeTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : outcomeTask.getResult()) {
                                                    String cate = documentSnapshot.getString("category");
                                                    String amount = documentSnapshot.getString("amount");
                                                    String date = documentSnapshot.getString("date");
                                                    amount = "-" + amount;
                                                    listItems.add(new ListItem(cate, amount, date));
                                                }

                                                // Sort listItems by date in descending order
                                                Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));

                                                // Set items to adapter
                                                mAdapter.setItems(listItems);
                                            }
                                        });
                            }
                        });
            }
        } else if (type == "income") {
            String userId = user.getUid();
            mStore.collection("user").document(userId).collection("income")
                    .get().addOnCompleteListener(task -> {
                        List<ListItem> listItems = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String cate = "Income";
                                String amount = documentSnapshot.getString("amount");
                                String date = documentSnapshot.getString("date");
                                amount = "+" + amount;
                                listItems.add(new ListItem(cate, amount, date));
                            }
                        }
                        Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                        mAdapter.setItems(listItems);
                    });
        } else {
            String userId = user.getUid();
            mStore.collection("user").document(userId).collection("outcome")
                    .get().addOnCompleteListener(task -> {
                        List<ListItem> listItems = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String cate = documentSnapshot.getString("category");
                                String amount = documentSnapshot.getString("amount");
                                String date = documentSnapshot.getString("date");
                                amount = "-" + amount;
                                listItems.add(new ListItem(cate, amount, date));
                            }
                        }
                        Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                        mAdapter.setItems(listItems);
                    });
        }
    }

    private int compare (String date1, String date2) {
        return date2.compareTo(date1);
    }

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        if (mContext.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        } else {
            throw new IllegalArgumentException("Attribute not found in theme");
        }
    }

    private void showList() {
        mlist = getResources().getStringArray(R.array.list_dialog);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setItems(mlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, " ### you clicked = " + mlist[i]);
                if (mlist[i].equals("Annually")) {
                    mListTitle.setText(R.string.list_year);
                }
                else if (mlist[i].equals("Monthly")) {
                    mListTitle.setText(R.string.list_month);
                }
                else if (mlist[i].equals("Weekly")) {
                    mListTitle.setText(R.string.list_week);
                }
                else if (mlist[i].equals("Daily")) {
                    mListTitle.setText(R.string.list_day);
                }
                else {
                    Log.d(TAG, "onClick: close dialog");
                }
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View view) {
        int themeColor = getThemeColor(android.R.attr.textColor);
        int secondColor = getThemeColor(android.R.attr.textColorSecondary);

        switch (view.getId()) {
            case R.id.all:
                Log.d(TAG, "onClick: all");
                fetchData("all");
                mAll.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                break;
            case R.id.expense:
                Log.d(TAG, "onClick: expense");
                fetchData("outcome");
                mExpense.setTextColor(themeColor);
                mAll.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                break;
            case R.id.income:
                Log.d(TAG, "onClick: income");
                fetchData("income");
                mIncome.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mAll.setTextColor(secondColor);
                break;
            case R.id.listBtn:
                Log.d(TAG, "onClick: 리스트 버튼 클릭했음");
                showList();
                break;
            default:
                Log.d(TAG, "onClick: 랄라라라라 디폴트");
                break;
        }
    }

    @Override
    public void onSwipeLeft(int position) {
        Log.d(TAG, "onSwipeLeft: 왼쪽으로 스와이핑 했습니다. ");
    }

    @Override
    public void onSwipeRight(int position) {
        Log.d(TAG, "onSwipeRight: 오른쪽으로 스와이핑 했습니다");
    }


}
