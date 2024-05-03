package com.example.mia_hometest.fragments.informations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;

public class UserInfoFragment extends Fragment {
    private final String TAG = UserInfoFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;
    private TextView mOff = null;

    public UserInfoFragment (Context context) {
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
        Log.d(TAG, " UserInfoFragment onCreateView: ");
        View view = inflater.inflate(R.layout.user_info, container, false);
        mGoback = view.findViewById(R.id.back);
        mOff = view.findViewById(R.id.offline);

        mOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(mContext, BaseActivity.class);
                intent.putExtra("logoff", true);
                startActivity(intent);
            }
        });

        mGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
                ((UserMainActivity) getActivity()).goBack();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " UserInfoFragment 끌게요");
        super.onDestroy();
    }
}
