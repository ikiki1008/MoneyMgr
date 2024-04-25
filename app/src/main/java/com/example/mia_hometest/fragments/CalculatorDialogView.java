package com.example.mia_hometest.fragments;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CalDialogView;
import com.prolificinteractive.materialcalendarview.CalendarDay;

public class CalculatorDialogView extends DialogFragment implements View.OnClickListener {
    private final String TAG = CalculatorDialogView.class.getSimpleName();
    private Context mContext = null;
    private AlertDialog.Builder mBuilder;
    private TextView mResult;
    private double mValue;
    private char mOper;

    public CalculatorDialogView (Context context) {
        mContext = context;
    }

    @Override
    public android.app.Dialog onCreateDialog (Bundle savedInstanceState) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.calculator, null);

        mBuilder = new AlertDialog.Builder(mContext);
        AlertDialog dialog = mBuilder.create();
        Window window = dialog.getWindow();

        if (window != null) {
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.gravity = Gravity.BOTTOM; //다이얼로그 화면 하단에 부착
            window.setAttributes(layoutParams);
        }

        mBuilder.setView(view);
        mBuilder.setCancelable(true);
        mResult = view.findViewById(R.id.result);

        int[] btn = {
                R.id.id1, R.id.id2, R.id.id3, R.id.id4, R.id.id5,
                R.id.id6, R.id.id7, R.id.id8, R.id.id9, R.id.zero,
                R.id.plus, R.id.minus, R.id.mul, R.id.div,
                R.id.equals, R.id.dot, R.id.back, R.id.ok
        };

        for (int num : btn) {
            TextView textView = view.findViewById(num);
            textView.setOnClickListener(this);
        }

        return mBuilder.create();
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onClick(View view) {
        String clickedNum = mResult.getText().toString(); // 현재 표시된 텍스트 가져오기

        switch (view.getId()) {
            case R.id.id1:
                mResult.setText(clickedNum + "1");
                break;
            case R.id.id2:
                mResult.setText(clickedNum + "2");
                break;
            case R.id.id3:
                mResult.setText(clickedNum + "3");
                break;
            case R.id.id4:
                mResult.setText(clickedNum + "4");
                break;
            case R.id.id5:
                mResult.setText(clickedNum + "5");
                break;
            case R.id.id6:
                mResult.setText(clickedNum + "6");
                break;
            case R.id.id7:
                mResult.setText(clickedNum + "7");
                break;
            case R.id.id8:
                mResult.setText(clickedNum + "8");
                break;
            case R.id.id9:
                mResult.setText(clickedNum + "9");
                break;
            case R.id.zero:
                mResult.setText(clickedNum + "0");
                break;
            case R.id.dot:
                mResult.setText(clickedNum + ".");
                break;

            case R.id.plus:
                if (!mResult.equals("")) {
                    Log.d(TAG, "onClick: plus numbers....");
                    mValue = Double.parseDouble(clickedNum);
                    mOper = '+';
                    mResult.setText("");
                }
                break;
            case R.id.mul:
                if (!mResult.equals("")) {
                    Log.d(TAG, "onClick: multiple numbers....");
                    mValue = Double.parseDouble(clickedNum);
                    mOper = '*';
                    mResult.setText("");
                }
                break;
            case R.id.minus:
                if (!mResult.equals("")) {
                    Log.d(TAG, "onClick: minus numbers.... ");
                    mValue = Double.parseDouble(clickedNum);
                    mOper = '-';
                    mResult.setText("");
                }
                break;
            case R.id.div:
                if (!mResult.equals("")) {
                    Log.d(TAG, "onClick: divided numbers.... ");
                    mValue = Double.parseDouble(clickedNum);
                    mOper = '/';
                    mResult.setText("");
                }
                break;
            case R.id.back:
                // 클리어 버튼을 클릭하면 텍스트를 지움
                mResult.setText("");
                mValue = 0;
                break;
            case R.id.equals:
                if (!clickedNum.equals("")) {
                    double mFinal = 0;
                    double thisValue = Double.parseDouble(mResult.getText().toString());
                    switch (mOper) {
                        case '+':
                            mFinal = mValue + thisValue;
                            break;
                        case '-':
                            mFinal = mValue - thisValue;
                            break;
                        case '*':
                            mFinal = mValue * thisValue;
                            break;
                        case '/':
                            mFinal = mValue / thisValue;
                            break;
                    }
                    int intValue = (int) mFinal;
                    mResult.setText(String.valueOf(intValue));
                    mValue = 0;
                }
                break;
            case R.id.ok:
                Log.d(TAG, "onClick: ");
                break;
            default:
                break;
        }
    }
}
