package com.example.mia_hometest.fragments.CalenderDialogs;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mia_hometest.R;

public class AccountDialogView extends DialogFragment implements View.OnClickListener{
    private static final String TAG = AccountDialogView.class.getSimpleName();
    private Context mContext = null;
    private Intent mIntent = new Intent();
    private String mType = "account";
    private CalCulListener mlistener;
    private AlertDialog.Builder mBuilder;

    private TextView mCash = null;
    private TextView mBank = null;
    private TextView mCard = null;
    private TextView mSave = null;
    private TextView mDelete = null;

    public AccountDialogView (Context context, CalCulListener listener) {
        mContext = context;
        mlistener = listener;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.account, null);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM); // 다이얼로그 뷰 하단에 부착

        mCash = view.findViewById(R.id.cash);
        mBank = view.findViewById(R.id.bank);
        mCard = view.findViewById(R.id.credit);
        mSave = view.findViewById(R.id.editBtn);
        mDelete = view.findViewById(R.id.cancelBtn);

        mCash.setOnClickListener(this);
        mBank.setOnClickListener(this);
        mCard.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mDelete.setOnClickListener(this);

        return dialog;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cash:
                case R.id.credit:
                case R.id.bank:
                    String text = ((TextView) view).getText().toString();
                    mIntent.putExtra("value", text);
                    mlistener.onClicked(mIntent, mType);
                    break;
            case R.id.editBtn:
                dismiss();
                break;
            case R.id.cancelBtn:
                mIntent.putExtra("value", "");
                mlistener.onClicked(mIntent, mType);
                break;
        }
    }
}
