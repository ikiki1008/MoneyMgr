package com.example.mia_hometest;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Locale;

public class BaseActivity extends FragmentActivity {
    private final String TAG = BaseActivity.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext;
    private Intent mIntent;
    private LoginFragment mLogin = null;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        setContentView(R.layout.base);
        init();

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        launchFragment(mLogin);

//        if (user != null) {
//            Log.d(TAG, "onCreate: 이미 로그인한 유저입니다. " + user.getEmail());
//            startActivity(new Intent(mContext, UserMainActivity.class));
//            finish();
//        } else {
//            Log.d(TAG, "onCreate: 로그인 하지 않은 유저입니다. ");
//            launchFragment(mLogin);
//        }
    }

    private void init() {
        mLogin = new LoginFragment(mContext);
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
