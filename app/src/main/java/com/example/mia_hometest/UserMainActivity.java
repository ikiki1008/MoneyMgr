package com.example.mia_hometest;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.example.mia_hometest.fragments.informations.UserInfoFragment;
import com.example.mia_hometest.fragments.main.CardScreenFragment;
import com.example.mia_hometest.fragments.main.ChartScreenFragment;
import com.example.mia_hometest.fragments.main.InfoScreenFragment;
import com.example.mia_hometest.fragments.main.MainScreenFragment;

import java.io.File;

public class UserMainActivity extends FragmentActivity {
    
    private static final String TAG = UserMainActivity.class.getSimpleName();
    private Context mContext;
    private CardScreenFragment mCardFragment = null;
    private MainScreenFragment mMainFragment = null;
    private InfoScreenFragment mInfoFragment = null;
    private ChartScreenFragment mChartFragment = null;
    private UserInfoFragment mUserInfoFragment = null;
    private ImageView[] mImage = new ImageView[5];
    private Intent mIntent = new Intent();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate: ");
        super.onCreate(savedInstanceState);
        mContext = this;
        mIntent = getIntent();

        String themeColor = mIntent.getStringExtra("color");
        if (themeColor != null) {
            if (themeColor.equals("red")) {
                mContext.setTheme(R.style.MiaAppThemeRed);
            } else if (themeColor.equals("orange")) {
                mContext.setTheme(R.style.MiaAppThemeOrange);
            } else if (themeColor.equals("yellow")) {
                mContext.setTheme(R.style.MiaAppThemeYellow);
            } else if (themeColor.equals("green")) {
                mContext.setTheme(R.style.MiaAppThemeGreen);
            } else if (themeColor.equals("blue")) {
                mContext.setTheme(R.style.MiaAppThemeBlue);
            } else if (themeColor.equals("gray")) {
                mContext.setTheme(R.style.MiaAppThemeGray);
            } else {
                mContext.setTheme(R.style.MiaAppTheme);
            }
        } else {
            Log.d(TAG, "onCreate: 아직은 인텐트 안받음");
            mContext.setTheme(R.style.MiaAppTheme);
        }

        setContentView(R.layout.main);
        initViews();

        File codeCacheDir = getCodeCacheDir();
        File dexOutputDir = codeCacheDir;
        dexOutputDir.setReadOnly();

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
        mImage[4] = findViewById(R.id.user);
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

        mImage[4].setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchFragment(mUserInfoFragment);
            }
        });
    }

    private void initFragments() {
        mMainFragment = new MainScreenFragment(mContext);
        mChartFragment = new ChartScreenFragment(mContext);
        mInfoFragment = new InfoScreenFragment(mContext);
        mCardFragment = new CardScreenFragment(mContext);
        mUserInfoFragment = new UserInfoFragment(mContext);
    }

    public void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    public void goBack() {
        Log.d(TAG, "onBackPressed: ");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}