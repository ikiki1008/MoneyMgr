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

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;

import java.util.Locale;

public class LanguageFragment extends Fragment {
    private final String TAG = LanguageFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;
    private TextView mEng = null;
    private TextView mKo = null;
    private String mCurrLang = "en";

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

        Intent intent = new Intent(mContext, UserMainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LanguageFragment 끌게요");
        super.onDestroy();
    }
}
