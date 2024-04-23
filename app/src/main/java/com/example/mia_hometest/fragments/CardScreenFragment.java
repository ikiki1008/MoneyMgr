package com.example.mia_hometest.fragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.TestGridAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CardScreenFragment extends Fragment {
    private final String TAG = CardScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private TextView mAll = null;
    private TextView mExpense = null;
    private TextView mIncome = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;

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

        mAll = view.findViewById(R.id.all);
        mExpense = view.findViewById(R.id.expense);
        mIncome = view.findViewById(R.id.income);

        mAdapter = new TestGridAdapter(mContext);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mAll.setTextColor(mContext.getColor(R.color.main_p));
        getData("AllItems.json");

        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.main_p));
                mAll.setTextColor(mContext.getColor(R.color.dirty_white));
                mIncome.setTextColor(mContext.getColor(R.color.dirty_white));
                getData("AllItems.json");
            }
        });

        mExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.dirty_white));
                mIncome.setTextColor(mContext.getColor(R.color.dirty_white));
                mExpense.setTextColor(mContext.getColor(R.color.main_p));
                getData("Expenses.json");
            }
        });

        mIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.dirty_white));
                mIncome.setTextColor(mContext.getColor(R.color.main_p));
                mExpense.setTextColor(mContext.getColor(R.color.dirty_white));
                getData("Incomes.json");
            }
        });

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
                String desc = item.getString("desc");
                String price = item.getString("price");
                String date = item.getString("date");

                listItems.add(new ListItem(title, desc, price, date));
            }

            mAdapter.setItems(listItems);
            inputStream.close();
            reader.close();

        } catch (Exception e) {
            Log.d(TAG, "getData false..... : " + e);
        }
    }
}
