package com.example.mia_hometest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private final String TAG = RegisterFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private static final Boolean mValue = true;
    private Context mContext = null;
    private TextView mCancel = null;
    private TextView mSave = null;
    private EditText mEmail = null;
    private EditText mPwd = null;
    private ImageView mMoneyImg = null;

    public RegisterFragment (Context context) {
        mContext = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " LoginFragment onCreateView: ");
        View view = inflater.inflate(R.layout.register, container, false);

        mSharedPreference = mContext.getSharedPreferences("check_login", Context.MODE_PRIVATE);
        mSave = view.findViewById(R.id.signBtn);
        mCancel = view.findViewById(R.id.cancelBtn);
        mEmail = view.findViewById(R.id.set_email);
        mPwd = view.findViewById(R.id.set_pwd);
        mMoneyImg = view.findViewById(R.id.moneyImg);
        GlideDrawableImageViewTarget gif = new GlideDrawableImageViewTarget(mMoneyImg); //gif setting
        Glide.with(mContext).load(R.drawable.money_stack).into(gif);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pwd = mPwd.getText().toString();

                if (email.isEmpty()) {
                    Log.d(TAG, " ##### you cannot login without typing yout id on textbox ");
                    Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                    mEmail.startAnimation(shake);
                }
                else if (pwd.isEmpty()) {
                    Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
                    mPwd.startAnimation(shake);
                }
                else {
                    addNewData();
                    SharedPreferences.Editor editor = mSharedPreference.edit();
                    editor.putBoolean("login", true);
                    editor.apply();
                    Intent intent = new Intent(mContext, UserMainActivity.class);
                    startActivity(intent);
                }
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).goBack();
            }
        });
        return view;
    }

    private void addNewData() {
        Log.d(TAG, " ##### put new data resource into the db");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("email", mEmail.getText().toString());
        user.put("name", mPwd.getText().toString());
        
        db.collection("user")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Documents added with ID : " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to add new data"));
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LoginFragment 끌게요");
        super.onDestroy();
    }
}
