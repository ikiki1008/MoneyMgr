package com.example.mia_hometest.fragments.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;

public class CardScreenInfoFragment extends Fragment {
    private static final String TAG = CardScreenInfoFragment.class.getSimpleName();

    private static final String ARG_TRANS = "trans";
    private static final String ARG_DATE = "date";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_ACCOUNT = "account";
    private static final String ARG_NOTE = "note";

    private String trans;
    private String date;
    private String amount;
    private String category;
    private String account;
    private String note;

    private TextView mTitle;
    private TextView mDate;
    private TextView mAmount;
    private TextView mCate;
    private TextView mAcc;
    private EditText mNote;
    private TextView mCateTitle;
    private ImageView mImage;
    private ImageView mGoback;

    public static CardScreenInfoFragment newInstance(String trans, String date, String amount, String account, String note) { //인스턴스를 두개를 만들어 각 용도에 맞춰 다른 인자들을 받는다
        CardScreenInfoFragment fragment = new CardScreenInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANS, trans);
        args.putString(ARG_DATE, date);
        args.putString(ARG_AMOUNT, amount);
        args.putString(ARG_ACCOUNT, account);
        args.putString(ARG_NOTE, note);
        fragment.setArguments(args);
        Log.d(TAG, "newInstance income == " + trans + date + amount +account + note);
        return fragment;
    }

    public static CardScreenInfoFragment newInstance(String trans, String date, String amount, String category, String account, String note) {
        CardScreenInfoFragment fragment = new CardScreenInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANS, trans);
        args.putString(ARG_DATE, date);
        args.putString(ARG_AMOUNT, amount);
        args.putString(ARG_CATEGORY, category);
        args.putString(ARG_ACCOUNT, account);
        args.putString(ARG_NOTE, note);
        fragment.setArguments(args);
        Log.d(TAG, "newInstance outcome == " + trans + date + amount +account + note + category);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            trans = args.getString("trans");
            date = args.getString("date");
            amount = args.getString("amount");
            category = args.getString("category");
            account = args.getString("account");
            note = args.getString("note");
        }
    }

    @SuppressLint({"MissingInflatedId", "ResourceAsColor"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "CardScreenFragment onCreateView: ");
        View view = inflater.inflate(R.layout.card_specific_screen, container, false);

        mGoback = view.findViewById(R.id.back);
        mTitle = view.findViewById(R.id.mainInfoTitle);
        mDate = view.findViewById(R.id.date);
        mAmount = view.findViewById(R.id.amount);
        mCate = view.findViewById(R.id.cate);
        mNote = view.findViewById(R.id.note);
        mAcc = view.findViewById(R.id.acc);
        mImage = view.findViewById(R.id.icon);
        mCateTitle = view.findViewById(R.id.cateTitle);

        if (trans.equals(getString(R.string.income))) {
            Log.d(TAG, "onCreateView: 인컴이라면");
            mCate.setVisibility(View.GONE);
            mCateTitle.setVisibility(View.GONE);
            mTitle.setText(R.string.income);
            mTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.income)); //색상값이 제대로 처리되지 않아 ContextCompat.getColor() 를 사용
            mImage.setImageResource(R.drawable.bunny);
        } else {
            Log.d(TAG, "onCreateView: 아웃컴 이라면");
            mCate.setText(category);
            mTitle.setText(R.string.expense);
            mTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.outcome));
            setImage(category);
        }

        mDate.setText(date);
        mAmount.setText(amount);
        mAcc.setText(account);
        mNote.setText(note);

        mGoback.setOnClickListener(view1 -> {
            Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
            ((UserMainActivity) getActivity()).goBack();
        });
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView: 초기화");
        mTitle = null;
        mDate = null;
        mAmount = null;
        mCate = null;
        mAcc = null;
        mNote = null;
        mCateTitle = null;
        mImage = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    private void setImage(String category) {
        // mCate의 값, 즉 실제 문자열 값을 기준으로 이미지 설정
        if (category.equals(getString(R.string.ott))) {
            mImage.setImageResource(R.drawable.netflix);
        } else if (category.equals(getString(R.string.social))) {
            mImage.setImageResource(R.drawable.confetti);
        } else if (category.equals(getString(R.string.food))) {
            mImage.setImageResource(R.drawable.donut);
        } else if (category.equals(getString(R.string.rent))) {
            mImage.setImageResource(R.drawable.house);
        } else if (category.equals(getString(R.string.phone))) {
            mImage.setImageResource(R.drawable.app);
        } else if (category.equals(getString(R.string.trans))) {
            mImage.setImageResource(R.drawable.vehicles);
        } else if (category.equals(getString(R.string.card))) {
            mImage.setImageResource(R.drawable.card);
        } else if (category.equals(getString(R.string.loan))) {
            mImage.setImageResource(R.drawable.tax);
        } else if (category.equals(getString(R.string.hospital))) {
            mImage.setImageResource(R.drawable.hospital);
        } else if (category.equals(getString(R.string.hobby))) {
            mImage.setImageResource(R.drawable.artist);
        } else if (category.equals(getString(R.string.sports))) {
            mImage.setImageResource(R.drawable.physical);
        } else if (category.equals(getString(R.string.edu))) {
            mImage.setImageResource(R.drawable.book);
        } else if (category.equals(getString(R.string.household))) {
            mImage.setImageResource(R.drawable.paperroll);
        }
    }
}
