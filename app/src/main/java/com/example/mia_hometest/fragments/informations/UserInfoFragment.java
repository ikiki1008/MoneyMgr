package com.example.mia_hometest.fragments.informations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;

public class UserInfoFragment extends Fragment {
    private final String TAG = UserInfoFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;

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
