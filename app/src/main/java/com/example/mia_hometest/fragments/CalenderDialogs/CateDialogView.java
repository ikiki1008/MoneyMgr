package com.example.mia_hometest.fragments.CalenderDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mia_hometest.R;

public class CateDialogView extends DialogFragment implements View.OnClickListener {
    private final String TAG = CateDialogView.class.getSimpleName();
    private Context mContext = null;
    private AlertDialog.Builder mBuilder;
    private CalCulListener mListener;
    private Intent mIntent = new Intent();
    private String mType = "category";

    public CateDialogView(Context context, CalCulListener listener) {
        mContext = context;
        mListener = listener;
    }

    @SuppressLint("MissingInflatedId")
    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.category, null);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM); // 다이얼로그 뷰 하단에 부착

        int[] textview = {
                R.id.food, R.id.rent, R.id.mobile, R.id.card,
                R.id.hospital, R.id.social, R.id.hobby, R.id.ott,
                R.id.household, R.id.trans, R.id.sports, R.id.loan,
                R.id.etc, R.id.education, R.id.editBtn, R.id.cancelBtn, R.id.shopping
        };

        for (int id : textview) {
            TextView text = view.findViewById(id);
            text.setOnClickListener(this);
        }

        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.food:
            case R.id.rent:
            case R.id.mobile:
            case R.id.card:
            case R.id.hospital:
            case R.id.social:
            case R.id.hobby:
            case R.id.ott:
            case R.id.household:
            case R.id.trans:
            case R.id.sports:
            case R.id.loan:
            case R.id.etc:
            case R.id.education:
            case R.id.shopping:
                String socialText = ((TextView) view).getText().toString();
                Log.d(TAG, "onClick: 무엇을 클릭했나요?? " + socialText);
                mIntent.putExtra("value", socialText);
                mListener.onClicked(mIntent, mType);
                break;
            case R.id.cancelBtn:
                Log.d(TAG, "onClick: 취소 버튼 클릭.......");
                mIntent.putExtra("value", " ");
                mListener.onClicked(mIntent, mType);
                dismiss();
                break;
            case R.id.editBtn:
                Log.d(TAG, "onClick: 저장버튼 클릭?????");
                dismiss();
                break;
        }
    }
}
