package com.example.mia_hometest.fragments.card;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.fragments.CalenderDialogs.AccountDialogView;
import com.example.mia_hometest.fragments.CalenderDialogs.CalCulListener;
import com.example.mia_hometest.fragments.CalenderDialogs.CalculatorDialogView;
import com.example.mia_hometest.fragments.CalenderDialogs.CateDialogView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class CardScreenInfoFragment extends Fragment implements View.OnClickListener, CalCulListener {
    private static final String TAG = CardScreenInfoFragment.class.getSimpleName();

    private static final String ARG_TRANS = "trans";
    private static final String ARG_DOCS = "document";
    private static final String ARG_DATE = "date";
    private static final String ARG_AMOUNT = "amount";
    private static final String ARG_CATEGORY = "category";
    private static final String ARG_ACCOUNT = "account";
    private static final String ARG_NOTE = "note";

    private String trans;
    private String docId;
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
    private ImageView mSave;
    private ImageView mEdit;
    private String mNoteString;
    private boolean mIsExpense;
    private boolean mIsEditMode;
    private CalculatorDialogView mCalDialog;
    private CateDialogView mCateDialog;
    private AccountDialogView mAccDialog;

    public static CardScreenInfoFragment newInstance(String trans, String document, String date, String amount, String account, String note) { //인스턴스를 두개를 만들어 각 용도에 맞춰 다른 인자들을 받는다
        CardScreenInfoFragment fragment = new CardScreenInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANS, trans);
        args.putString(ARG_DOCS, document);
        args.putString(ARG_DATE, date);
        args.putString(ARG_AMOUNT, amount);
        args.putString(ARG_ACCOUNT, account);
        args.putString(ARG_NOTE, note);
        fragment.setArguments(args);
        Log.d(TAG, "newInstance income == " + trans + date + amount +account + note);
        return fragment;
    }

    public static CardScreenInfoFragment newInstance(String trans, String document, String date, String amount, String category, String account, String note) {
        CardScreenInfoFragment fragment = new CardScreenInfoFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TRANS, trans);
        args.putString(ARG_DOCS, document);
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
            docId = args.getString("document");
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
        View view;

        if (trans.equals(getString(R.string.expense))) {
            view = inflater.inflate(R.layout.card_specific_screen, container, false);
            mCate = view.findViewById(R.id.cate);
            mIsExpense = true;
        } else {
            view = inflater.inflate(R.layout.card_specific_screen_income, container, false);
        }

        mGoback = view.findViewById(R.id.back);
        mTitle = view.findViewById(R.id.mainInfoTitle);
        mDate = view.findViewById(R.id.date);
        mAmount = view.findViewById(R.id.amount);
        mNote = view.findViewById(R.id.note);
        mAcc = view.findViewById(R.id.acc);
        mImage = view.findViewById(R.id.icon);
        mCateTitle = view.findViewById(R.id.cateTitle);
        mSave = view.findViewById(R.id.save_list_btn);
        mEdit = view.findViewById(R.id.edit_list_btn);

        if (!mIsExpense) {
            Log.d(TAG, "onCreateView: 인컴이라면");
            mTitle.setText(R.string.income);
            mTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.income)); //색상값이 제대로 처리되지 않아 ContextCompat.getColor() 를 사용
            mImage.setImageResource(R.drawable.bunny);
        } else {
            Log.d(TAG, "onCreateView: 아웃컴 이라면");
            mCate.setText(category);
            mTitle.setText(R.string.expense);
            mTitle.setTextColor(ContextCompat.getColor(requireContext(), R.color.outcome));
            setImage(category);
            mCate.setOnClickListener(this);
        }

        mDate.setText(date);
        mAmount.setText(amount);
        mAcc.setText(account);
        mNote.setText(note);
        mNote.setInputType(InputType.TYPE_NULL);
        mNoteString = getContext().getString(R.string.note_desc);

        mGoback.setOnClickListener(this);
        mEdit.setOnClickListener(this);
        mSave.setOnClickListener(this);
        mDate.setOnClickListener(this);
        mAmount.setOnClickListener(this);
        mAcc.setOnClickListener(this);
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
            mImage.setImageResource(R.drawable.shopping);
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
        } else {
            mImage.setImageResource(R.drawable.placeholder);
        }
    }

    private void updateDb() {
        Log.d(TAG, "updateDb: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            if (mIsExpense) {
                if (docId != null) { // 문서 ID가 존재하는 경우
                    db.collection("user").document(userId).collection("outcome").document(docId)
                            .update(
                                    "note", mNote.getText().toString(),
                                    "category", mCate.getText().toString(),
                                    "account", mAcc.getText().toString(),
                                    "amount", mAmount.getText().toString()
                            )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: 노트 업데이트 성공...");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e);
                                }
                            });
                }
            } else {
                if (docId != null) { // 문서 ID가 존재하는 경우
                    db.collection("user").document(userId).collection("income").document(docId)
                            .update(
                                    "note", mNote.getText().toString(),
                                    "account", mAcc.getText().toString(),
                                    "amount", mAmount.getText().toString()
                            )
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d(TAG, "onSuccess: 노트 업데이트 성공...");
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d(TAG, "onFailure: " + e);
                                }
                            });
                }
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.date:
                break;
            case R.id.amount:
                Log.d(TAG, "onClick: amount111");
                if (mIsEditMode) {
                    Log.d(TAG, "onClick: amount");
                    mCalDialog = new CalculatorDialogView(getContext(), this);
                    mCalDialog.show(getParentFragmentManager(), "child_fragment");
                }
                break;
            case R.id.cate:
                Log.d(TAG, "onClick: cate 1111");
                if (mIsEditMode) {
                    Log.d(TAG, "onClick: cate 2222");
                    mCateDialog = new CateDialogView(getContext(), this);
                    mCateDialog.show(getParentFragmentManager(), "child_fragment");
                }
                break;
            case R.id.acc:
                Log.d(TAG, "onClick: acc 111");
                if (mIsEditMode) {
                    Log.d(TAG, "onClick: acc 22222");
                    mAccDialog = new AccountDialogView(getContext(), this);
                    mAccDialog.show(getParentFragmentManager(), "child_fragment");
                }
                break;
            case R.id.back:
                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
                ((UserMainActivity) getActivity()).goBack();
                break;
            case R.id.edit_list_btn:
                Log.d(TAG, "onClick: 수정 모드 시작");
                mIsEditMode = true;
                mEdit.setVisibility(View.GONE);
                mSave.setVisibility(View.VISIBLE);
                mNote.setInputType(InputType.TYPE_CLASS_TEXT);
                mNote.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        mNoteString = mNote.getText().toString();
                        Log.d(TAG, "onTextChanged: " + mNoteString);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        Log.d(TAG, "afterTextChanged: ");
                    }
                });
                break;
            case R.id.save_list_btn:
                Log.d(TAG, "onClick: 수정 완료");
                if (mIsExpense) {
                    Log.d(TAG, "onClick: 1");
                    if (mAcc.getText().toString().equals("") || mCate.getText().toString().equals("")) {
                        Log.d(TAG, "onClick: 2");
                        Toast.makeText(getContext(), R.string.warn_notnull_account, Toast.LENGTH_SHORT).show();
                    } else if (mAcc.getText().equals("") && mCate.getText().equals("")) {
                        Log.d(TAG, "onClick: 2.5");
                        Toast.makeText(getContext(), R.string.warn_notnull_account, Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(TAG, "onClick: 3");
                        updateDb();
                        mIsEditMode = false;
                        mEdit.setVisibility(View.VISIBLE);
                        mSave.setVisibility(View.GONE);
                        mNote.setInputType(InputType.TYPE_NULL);
                    }
                } else {
                    if (mAcc.getText().equals("") || mAcc == null) {
                        Toast.makeText(getContext(), R.string.warn_notnull_account, Toast.LENGTH_SHORT).show();
                    } else {
                        updateDb();
                        mIsEditMode = false;
                        mEdit.setVisibility(View.VISIBLE);
                        mSave.setVisibility(View.GONE);
                        mNote.setInputType(InputType.TYPE_NULL);
                    }
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onClicked(Intent intent, String type) {
        switch (type) {
            case "calculator":
                int value = intent.getIntExtra("value", 0);
                String newSum = String.valueOf(value);
                Log.d(TAG, "값이 뭔가요 " + newSum);
                mAmount.setText(newSum);
                break;
            case "category":
                String newCate = intent.getStringExtra("value");
                Log.d(TAG, "onClicked: 뭔데 이거 .... " + newCate);
                mCate.setText(newCate);
                if (newCate.equals("")) {
                    setImage("null");
                } else {
                    setImage(newCate);
                }
                break;
            case "account":
                String newAcc = intent.getStringExtra("value");
                mAcc.setText(newAcc);
                break;
            default:
                break;
        }
    }
}
