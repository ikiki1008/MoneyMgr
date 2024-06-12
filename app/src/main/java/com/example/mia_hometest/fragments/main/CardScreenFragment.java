package com.example.mia_hometest.fragments.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardScreenFragment extends Fragment implements View.OnClickListener{
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

    public void fetchData(String type) {
        mStore.collection("user").get().addOnCompleteListener(task -> {
            List<ListItem> listItems = new ArrayList<>();
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                    String cate = documentSnapshot.getString("category");
                    String price = documentSnapshot.getString("price");
                    String date = documentSnapshot.getString("date");
                    listItems.add(new ListItem(cate, price, date));
                }
            } else {
                String cate = "null";
                String price = "0000";
                String date = "0000.00.00";
                listItems.add(new ListItem(cate, price, date));
            }
            mAdapter.setItems(listItems);
        });
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " CardScreenFragment 끌게요");
        super.onDestroy();
    }

    public CardScreenFragment (Context context) {
        mContext = context;
    }

    public void getData(String fileName) {
        try {
            InputStream inputStream = mContext.getAssets().open(fileName);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }

            JSONObject jsonObject = new JSONObject(stringBuilder.toString());
            JSONArray jsonArray = jsonObject.getJSONArray("All_items");

            List<ListItem> listItems = new ArrayList<>();
            for (int i=0; i<jsonArray.length(); i++) {
                JSONObject item = jsonArray.getJSONObject(i);
                String title = item.getString("title");
//                String desc = item.getString("desc");
                String price = item.getString("price");
                String date = item.getString("date");

                listItems.add(new ListItem(title, price, date));
            }

            mAdapter.setItems(listItems);
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            Log.d(TAG, "getData false..... : " + e);
        }
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
                Log.d(TAG, "onClick: alllllllllll");
                fetchData("all");
                mAll.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                break;
            case R.id.expense:
                Log.d(TAG, "onClick: expense");
                fetchData("all");
                mExpense.setTextColor(themeColor);
                mAll.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                break;
            case R.id.income:
                Log.d(TAG, "onClick: income");
                fetchData("all");
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
}
