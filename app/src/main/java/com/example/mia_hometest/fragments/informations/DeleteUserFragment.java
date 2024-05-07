package com.example.mia_hometest.fragments.informations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.mia_hometest.BaseActivity;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;

public class DeleteUserFragment extends Fragment {
    private final String TAG = DeleteUserFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext = null;
    private ImageView mGoback = null;
    private TextView mLogout = null;

    public DeleteUserFragment (Context context) {
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
        Log.d(TAG, " DeleteUserFragment onCreateView: ");
        View view = inflater.inflate(R.layout.category, container, false);
        mSharedPreference = mContext.getSharedPreferences("check_login", Context.MODE_PRIVATE);

//        mGoback = view.findViewById(R.id.back);
////        mLogout = view.findViewById(R.id.offline);
//
//        mGoback.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
//                ((UserMainActivity) getActivity()).goBack();
//            }
//        });

//        mLogout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Log.d(TAG, "onClick: 로그아웃 하자....");
//                SharedPreferences.Editor editor = mSharedPreference.edit();
//                editor.putBoolean("login", false);
//                editor.apply();
//                Intent intent = new Intent(mContext, BaseActivity.class);
//                startActivity(intent);
//                Log.d(TAG, "onClick: 됏나??");
//            }
//        });
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " DeleteUserFragment 끌게요");
        super.onDestroy();
    }
}
