package com.example.mia_hometest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext = null;
    private TextView mRegister = null;
    private TextView mLogin = null;
    private RegisterFragment mRegisFragment = null;

    public LoginFragment (Context context) {
        mContext = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "LoginFragment onAttach: ");
        mContext = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " LoginFragment onCreateView: ");
        View view = inflater.inflate(R.layout.login, container, false);

        mRegisFragment = new RegisterFragment(mContext);
        mSharedPreference = mContext.getSharedPreferences("check_login", Context.MODE_PRIVATE);

        mLogin = view.findViewById(R.id.save_title);
        mRegister = view.findViewById(R.id.regis_title);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 클릭했다 로그인하자");
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean("login", true);
                editor.apply();
                Intent intent = new Intent(mContext, UserMainActivity.class);
                startActivity(intent);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).launchFragment(mRegisFragment);
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LoginFragment 끌게요");
        super.onDestroy();
    }
}
