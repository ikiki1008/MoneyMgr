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

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.category, null);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM);

        return dialog;
    }

    @Override
    public void onClick(View view) {

    }
}
