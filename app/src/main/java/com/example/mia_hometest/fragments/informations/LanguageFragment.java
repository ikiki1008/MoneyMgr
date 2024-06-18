package com.example.mia_hometest.fragments.informations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mia_hometest.BaseActivity;
import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;

import java.util.Locale;

public class LanguageFragment extends Fragment {
    private final String TAG = LanguageFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;
    private ImageView mBar = null;
    private ImageView mBarKr = null;
    private ImageView mCheck = null;
    private ImageView mCheckKr = null;
    private TextView mEng = null;
    private TextView mKo = null;
    private String mCurrLang = "en";
    private final String CHANGE_LANGUAGE = "com.android.CHANGE_LANGUAGE";

    public LanguageFragment (Context context) {
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
        Log.d(TAG, " LanguageFragment onCreateView: ");
        View view = inflater.inflate(R.layout.lang, container, false);

        mEng = view.findViewById(R.id.btn_eng);
        mKo = view.findViewById(R.id.btn_ko);
        mGoback = view.findViewById(R.id.back);
        mBar = view.findViewById(R.id.bar1);
        mBarKr = view.findViewById(R.id.bar2);
        mCheckKr = view.findViewById(R.id.check2);
        mCheck = view.findViewById(R.id.check);
        mGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
                ((UserMainActivity) getActivity()).goBack();
            }
        });

        mEng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrLang = "en";
                changeLanguage(mCurrLang);
            }
        });

        mKo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mCurrLang = "ko";
                changeLanguage(mCurrLang);
            }
        });

        return view;
    }

    private void changeLanguage(String value) {
        if (value.equals("en")) {
            Log.d(TAG, "changeLanguage: 영어로 바꾸기");
            Locale eng = Locale.US;
            Configuration configuration = new Configuration();
            configuration.locale = eng;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        } else {
            Log.d(TAG, "changeLanguage: 한국어로 바꾸기");
            Locale kor = Locale.KOREA;
            Configuration configuration = new Configuration();
            configuration.locale = kor;
            getResources().updateConfiguration(configuration, getResources().getDisplayMetrics());
        }

        Intent intent = new Intent(CHANGE_LANGUAGE);
        intent.putExtra("language", value);
        LocalBroadcastManager.getInstance(requireContext()).sendBroadcast(intent);
        Log.d(TAG, "changeLanguage: 현재 보내는 value >>>> " + value);

//        Intent intent = new Intent(mContext, BaseActivity.class);
//        intent.putExtra("language", value);
//        Log.d(TAG, "changeLanguage: 현재 보내는 value >>>> " + value);
//        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LanguageFragment 끌게요");
        super.onDestroy();
    }
}
