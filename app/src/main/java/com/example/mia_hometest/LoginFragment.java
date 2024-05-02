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
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext = null;
    private TextView mRegister = null;
    private TextView mLogin = null;
    private EditText mEmail = null;
    private EditText mPwd = null;
    private ImageView mPiggy = null;
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

        mLogin = view.findViewById(R.id.loginBtn);
        mRegister = view.findViewById(R.id.registerBtn);
        mEmail = view.findViewById(R.id.set_email);
        mPwd = view.findViewById(R.id.set_pwd);
        mPiggy = view.findViewById(R.id.piggy);
        GlideDrawableImageViewTarget gif = new GlideDrawableImageViewTarget(mPiggy); //gif setting
        Glide.with(mContext).load(R.drawable.piggy_money).into(gif);

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pwd = mPwd.getText().toString();

                if (email.isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                    mEmail.startAnimation(shake);
                }
                else if (pwd.isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                    mPwd.startAnimation(shake);
                }
                else {
                    Log.d(TAG, "onClick: 클릭했다 로그인하자");
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putBoolean("login", true);
                    editor.apply();
                    Intent intent = new Intent(mContext, UserMainActivity.class);
                    startActivity(intent);
                }
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
