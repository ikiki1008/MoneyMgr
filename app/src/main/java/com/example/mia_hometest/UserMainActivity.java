package com.example.mia_hometest;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.anychart.core.Base;
import com.example.mia_hometest.fragments.informations.LanguageFragment;
import com.example.mia_hometest.fragments.informations.UserInfoFragment;
import com.example.mia_hometest.fragments.main.CardScreenFragment;
import com.example.mia_hometest.fragments.main.ChartScreenFragment;
import com.example.mia_hometest.fragments.main.InfoScreenFragment;
import com.example.mia_hometest.fragments.main.MainScreenFragment;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.util.Locale;

public class UserMainActivity extends FragmentActivity{
    
    private static final String TAG = UserMainActivity.class.getSimpleName();
    private Context mContext;
    private CardScreenFragment mCardFragment = null;
    private MainScreenFragment mMainFragment = null;
    private InfoScreenFragment mInfoFragment = null;
    private ChartScreenFragment mChartFragment = null;
    private UserInfoFragment mUserInfoFragment = null;
    private ImageView[] mImage = new ImageView[5];
    private Intent mIntent = new Intent();
    private SharedPreferences mPreference;
    private SharedPreferences mLanguagePreferene;
    private ActivityResultLauncher<Intent> mImageUpdate;
    private final String CHANGE_LANGUAGE = "com.android.CHANGE_LANGUAGE";
    private final BroadcastReceiver mLanguageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(CHANGE_LANGUAGE)) {
                String value = intent.getStringExtra("language");
                if (value != null) {
                    SharedPreferences.Editor editor = mLanguagePreferene.edit();
                    editor.putString("user_lang", value);
                    editor.apply();
                    Log.d(TAG, "saveLanguage: 일단 여기까지 됐는지 확인 == " + value);
                    startActivity(new Intent(mContext, BaseActivity.class));
                    finish();
                }
            }
        }
    };

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: ");
        mContext = this;
        mIntent = getIntent();
        mPreference = getSharedPreferences("theme", MODE_PRIVATE);
        String userColor = mPreference.getString("color", null);
        String themeColor = mIntent.getStringExtra("color");
        mLanguagePreferene = getSharedPreferences("language", MODE_PRIVATE);
        String previousLang = mLanguagePreferene.getString("user_lang", null);
        LocalBroadcastManager.getInstance(this).registerReceiver(mLanguageReceiver, new IntentFilter(CHANGE_LANGUAGE));

        if (previousLang != null) {
            Log.d(TAG, "onCreate: set language as saved");
            setLanguage(previousLang);
        } else {
            Log.d(TAG, "onCreate: shaedPreference 에 아무것도 없다..");
            setLanguage("en");
        }

        if (themeColor != null) {
            setTheme(themeColor);
            savePreference(themeColor);
        } else {
            if (userColor != null) {
                setTheme(userColor);
            } else {
                mContext.setTheme(R.style.MiaAppTheme);
            }
        }

        setContentView(R.layout.main);
        initViews();

        File codeCacheDir = getCodeCacheDir();
        File dexOutputDir = codeCacheDir;
        dexOutputDir.setReadOnly();

        mImageUpdate = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        Log.d(TAG, "onCreate: 데이터를 성공적으로 가져 왔다면");
                        Uri uri = result.getData().getData();
                        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                        if (fragment instanceof  UserInfoFragment) {
                            Log.d(TAG, "onCreate: 프래그먼트에 전달");
                            ((UserInfoFragment) fragment).imageResult(uri);
                        }
                    } else {
                        Log.d(TAG, "onCreate: 실패했습니다.");
                    }
                }
        );

        //첫화면 loading
        initFragments();
        launchFragment(mMainFragment);
        //이 후
        onCLick();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        String previousLang = mLanguagePreferene.getString("user_lang", null);
        if (previousLang != null) {
            Log.d(TAG, "onCreate: set language as saved");
            setLanguage(previousLang);
        } else {
            Log.d(TAG, "onCreate: shaedPreference 에 아무것도 없다..");
            setLanguage("en");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mLanguageReceiver);
    }

    private void setLanguage(String lang) {
        Log.d(TAG, "setLanguage: Setting language to " + lang);
        if (lang.equals("en")) {
            Log.d(TAG, "changeLanguage: 영어로 바꾸기");
            Locale eng = Locale.US;
            Configuration configuration = new Configuration();
            configuration.locale = eng;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        } else if (lang.equals("ko")) {
            Log.d(TAG, "changeLanguage: 한국어로 바꾸기");
            Locale kor = Locale.KOREA;
            Configuration configuration = new Configuration();
            configuration.locale = kor;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }
    }

    private void setTheme(String themeColor) {
        Log.d(TAG, "setTheme: ");
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
    }

    private void savePreference(String color) {
        Log.d(TAG, "savePreference: ");
        SharedPreferences.Editor editor = mPreference.edit();
        editor.putString("color", color);
        editor.apply();
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

    public ActivityResultLauncher<Intent> getImageUpdate() {
        Log.d(TAG, "getImageUpdate: ");
        return mImageUpdate;
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