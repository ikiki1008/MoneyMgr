package com.example.mia_hometest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

public class BaseActivity extends FragmentActivity {
    private final String TAG = BaseActivity.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext;
    private Intent mIntent;
    private LoginFragment mLogin = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.base);
        init();

        launchFragment(mLogin);

//        if (isLoggedIn()) {
//            Log.d(TAG, "onCreate: no user ... lets create new user");
//            launchFragment(mLogin);
//        } else {
//            Log.d(TAG, "onCreate: user is logged in... ");
//            mIntent = new Intent(mContext, UserMainActivity.class);
//            startActivity(mIntent);
//        }
    }

    private void init() {
        mLogin = new LoginFragment(mContext);
    }

    private boolean isLoggedIn() {
        mSharedPreference = getSharedPreferences("check_login", MODE_PRIVATE);
        return mSharedPreference.getBoolean("login", false);
    }

    public void goBack() {
        Log.d(TAG, "onBackPressed: ");
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void launchFragment(Fragment fragment) {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
