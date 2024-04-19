package com.example.mia_hometest;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.TestGridAdapter;
import com.example.mia_hometest.graph.MonthFragment;
import com.example.mia_hometest.graph.WeekFragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {
    
    private static final String TAG = MainActivity.class.getSimpleName();
    private Context mContext;
    private WeekFragment mWeekFragment = null;
    private MonthFragment mMonthFragment = null;

    private ImageView mWeek;
    private ImageView mMonth;
    private TextView mText;
    private TextView mAll;
    private TextView mExpense;
    private TextView mIncome;
    private TextView mRight;
    private TextView mLeft;

    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.main);

        mWeek = findViewById(R.id.imgWeek);
        mMonth = findViewById(R.id.imgMonth);
        mText = findViewById(R.id.tv);
        mAll = findViewById(R.id.all);
        mExpense = findViewById(R.id.expense);
        mIncome = findViewById(R.id.income);
        mLeft = findViewById(R.id.left);
        mRight = findViewById(R.id.right);

        mAdapter = new TestGridAdapter(mContext);
        mRecyclerView = findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        //첫화면 loading
        initFragments();
        launchFirstScreen();

        //이 후
        onCLick();
    }

    private void launchFirstScreen() {
        getData("AllItems.json");
        mWeek.setImageResource(R.drawable.week_frame);
        mText.setText(R.string.week_title);
        launchFragment(mWeekFragment);
        mLeft.setText(R.string.left_week);
        mRight.setText(R.string.right_week);
    }

    private void onCLick() {
        mAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.navy));
                mIncome.setTextColor(mContext.getColor(R.color.normal));
                mExpense.setTextColor(mContext.getColor(R.color.normal));
                getData("AllItems.json");
            }
        });

        mIncome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.normal));
                mIncome.setTextColor(mContext.getColor(R.color.navy));
                mExpense.setTextColor(mContext.getColor(R.color.normal));
                getData("Incomes.json");
            }
        });

        mExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAll.setTextColor(mContext.getColor(R.color.normal));
                mIncome.setTextColor(mContext.getColor(R.color.normal));
                mExpense.setTextColor(mContext.getColor(R.color.navy));
                getData("Expenses.json");
            }
        });

        mWeek.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " #### onClick: week");
                launchFragment(mWeekFragment);
                mWeek.setImageResource(R.drawable.week_frame);
                mMonth.setImageResource(R.drawable.month_unframe);
                mText.setText(R.string.week_title);
                mLeft.setText(R.string.left_week);
                mRight.setText(R.string.right_week);
            }
        });

        mMonth.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, " #### onClick: month");
                launchFragment(mMonthFragment);
                mMonth.setImageResource(R.drawable.month_frame);
                mWeek.setImageResource(R.drawable.week_unframe);
                mText.setText(R.string.month_title);
                mLeft.setText(R.string.left_month);
                mRight.setText(R.string.left_month);
            }
        });
    }

    public void getData(String fileName) {
        try {
            InputStream inputStream = getAssets().open(fileName);
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

    private void initFragments() {
        mWeekFragment = new WeekFragment(mContext);
        mMonthFragment = new MonthFragment(mContext);
    }

    public void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

    }
}