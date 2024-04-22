package com.example.mia_hometest;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mia_hometest.fragments.CardScreenFragment;
import com.example.mia_hometest.fragments.ChartScreenFragment;
import com.example.mia_hometest.fragments.InfoScreenFragment;
import com.example.mia_hometest.fragments.MainScreenFragment;
import com.example.mia_hometest.fragments.MonthFragment;
import com.example.mia_hometest.fragments.WeekFragment;

public class BaseActivity extends FragmentActivity {
    
    private static final String TAG = BaseActivity.class.getSimpleName();
    private Context mContext;
    private WeekFragment mWeekFragment = null;
    private MonthFragment mMonthFragment = null;
    private CardScreenFragment mCardFragment = null;
    private MainScreenFragment mMainFragment = null;
    private InfoScreenFragment mInfoFragment = null;
    private ChartScreenFragment mChartFragment = null;

    private ImageView[] mImage = new ImageView[4];

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.main);
        initViews();

        //첫화면 loading
        initFragments();
        launchFragment(mMainFragment);

        //이 후
        onCLick();
    }

    private void initViews() {
        mImage[0] = findViewById(R.id.mainScreen);
        mImage[1] = findViewById(R.id.expenseScreen);
        mImage[2] = findViewById(R.id.chartScreen);
        mImage[3] = findViewById(R.id.infoScreen);
    }

    private void onCLick() {
        mImage[0].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(mMainFragment);
            }
        });


        mImage[1].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(mCardFragment);
            }
        });

        mImage[2].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(mChartFragment);
            }
        });

        mImage[3].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(mInfoFragment);
            }
        });
    }

    private void initFragments() {
        mMainFragment = new MainScreenFragment(mContext);
        mChartFragment = new ChartScreenFragment(mContext);
        mInfoFragment = new InfoScreenFragment(mContext);
        mCardFragment = new CardScreenFragment(mContext);
    }

    public void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();

    }
}