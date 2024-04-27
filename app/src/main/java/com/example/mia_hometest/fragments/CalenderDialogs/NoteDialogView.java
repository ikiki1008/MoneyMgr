package com.example.mia_hometest.fragments.CalenderDialogs;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.mia_hometest.R;

public class NoteDialogView extends DialogFragment {
    private static final String TAG = NoteDialogView.class.getSimpleName();
    private Context mContext = null;
    private CalCulListener mListener;
    private AlertDialog.Builder mBuilder;
    private Intent mIntent = new Intent();
    private String mType = "note";
    private EditText mNote = null;
    private TextView mSave = null;
    private TextView mCancel = null;
    private String mUserNote = null;

    public NoteDialogView (Context context, CalCulListener listener) {
        mContext = context;
        mListener = listener;
    }

    @NonNull
    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.note, null);

        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setView(view);
        mBuilder.setCancelable(true);

        AlertDialog dialog = mBuilder.create();
        dialog.getWindow().setGravity(Gravity.BOTTOM); // 다이얼로그 뷰 하단에 부착

        mNote = view.findViewById(R.id.note);
        mSave = view.findViewById(R.id.editBtn);
        mCancel = view.findViewById(R.id.cancelBtn);

        mNote.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                mUserNote = mNote.getText().toString();
                Log.d(TAG, " mUserNote == " + mUserNote);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserNote.isEmpty()) {
                    Log.d(TAG, "onClick: 노트가 비어있지 않다면 보내기");
                    mIntent.putExtra("value", mUserNote);
                    mListener.onClicked(mIntent, mType);
                } else {
                    Log.d(TAG, "onClick: note is null.... user hasn't write anything");
                }
                dismiss();
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!mUserNote.isEmpty()) {
                    Log.d(TAG, "onClick: 노트가 비어있지 않다면 다 지워버리기");
                    mNote.setText("");
                    mUserNote = "";

                    mIntent.putExtra("value", mUserNote);
                    mListener.onClicked(mIntent, mType);
                } else {
                    Log.d(TAG, "onClick: user hasn't write anything .... 2222 ");
                }
                dismiss();
            }
        });


        return dialog;
    }
}
