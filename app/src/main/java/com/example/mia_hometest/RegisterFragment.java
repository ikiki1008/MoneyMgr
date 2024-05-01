package com.example.mia_hometest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private final String TAG = RegisterFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext = null;
    private TextView mCancel = null;
    private TextView mSave = null;
    private EditText mEmail = null;
    private EditText mName = null;

    public RegisterFragment (Context context) {
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
        Log.d(TAG, " LoginFragment onCreateView: ");
        View view = inflater.inflate(R.layout.register, container, false);

        mSharedPreference = mContext.getSharedPreferences("check_login", Context.MODE_PRIVATE);
        mSave = view.findViewById(R.id.save_title);
        mCancel = view.findViewById(R.id.cancel);
        mEmail = view.findViewById(R.id.set_email);
        mName = view.findViewById(R.id.set_name);

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewData();
                SharedPreferences.Editor editor = mSharedPreference.edit();
                editor.putBoolean("login", true);
                editor.apply();
                Intent intent = new Intent(mContext, UserMainActivity.class);
                startActivity(intent);
            }
        });

        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).goBack();
            }
        });
        return view;
    }

    private void addNewData() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("email", mEmail.getText().toString());
        user.put("name", mName.getText().toString());
        
        db.collection("user")
                .add(user)
                .addOnSuccessListener(documentReference -> Log.d(TAG, "Documents added with ID : " + documentReference.getId()))
                .addOnFailureListener(e -> Log.d(TAG, "Failed to add new data"));
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LoginFragment 끌게요");
        super.onDestroy();
    }
}
