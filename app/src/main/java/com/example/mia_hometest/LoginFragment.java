package com.example.mia_hometest;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mia_hometest.common.UserNameItem;
import com.example.mia_hometest.common.ThemeListAdapter;
import com.google.android.gms.auth.api.identity.BeginSignInRequest;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class LoginFragment extends Fragment {
    private static final String TAG = LoginFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private Context mContext = null;
    private TextView mRegister = null;
    private TextView mLogin = null;
    private EditText mEmail = null;
    private EditText mPwd = null;
    private ImageView mPiggy = null;
    private RegisterFragment mRegisFragment = null;
    private SignInButton mGoogleLoginBtn = null;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private ThemeListAdapter<UserNameItem> mAdapter;
    private List<UserNameItem> mItemList = new ArrayList<>();

    public LoginFragment(Context context) {
        mContext = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "LoginFragment onAttach: ");
        mContext = context;
    }

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " LoginFragment onCreateView: ");
        View view = inflater.inflate(R.layout.login, container, false);

        mRegisFragment = new RegisterFragment(mContext);
        mSharedPreference = mContext.getSharedPreferences("check_login", MODE_PRIVATE);
        mLogin = view.findViewById(R.id.loginBtn);
        mRegister = view.findViewById(R.id.registerBtn);
        mEmail = view.findViewById(R.id.set_email);
        mPwd = view.findViewById(R.id.setPwd);
        mPiggy = view.findViewById(R.id.piggy);
        mGoogleLoginBtn = view.findViewById(R.id.google_login_btn);

        // Glide 초기화
        Glide.with(this)
                .load(R.drawable.piggy_money)
                .into(mPiggy);

        googleSignInit();

        mGoogleLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
            }
        });

        mLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String pwd = mPwd.getText().toString();

                if (email.isEmpty()) {
                    startShake(mEmail);
                } else if (pwd.isEmpty()) {
                    startShake(mPwd);
                } else {
                    Log.d(TAG, "onClick: 클릭했다 로그인하자");
                    emailSignIn(email, pwd);
                }
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).launchFragment(mRegisFragment);
                mEmail.setText("");
                mPwd.setText("");
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " LoginFragment 끌게요");
        super.onDestroy();
    }

    private void startShake(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        editText.startAnimation(shake);
    }

    private void googleSignInit() {
        Log.d(TAG, "googleSignInit: ");
        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            Intent intent = result.getData();
                            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(intent);

                            try {
                                GoogleSignInAccount account = task.getResult(ApiException.class);
                                firebaseAuthGoogle(account);

                            } catch (ApiException e) {
                                Log.d(TAG, "뭔가 또 실패... " + e);
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        configSignIn();
        mAuth = FirebaseAuth.getInstance();
    }

    private void firebaseAuthGoogle(GoogleSignInAccount account) {
        Log.d(TAG, "firebaseAuthGoogle: ");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Activity) mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "onComplete: 로그인 성공....");
                            FirebaseUser firebaseUser = mAuth.getCurrentUser();
                            mEmail.setText(firebaseUser.getEmail());
                            checkGoogleName();
                        } else {
                            Log.d(TAG, "#### 로그인 실패...");
                            startShake(mEmail);
                        }
                    }
                });
    }

    private void googleSignIn() {
        Log.d(TAG, "googleSignIn: ");
        Intent intent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

    private void configSignIn() {
        Log.d(TAG, "configSignIn: ");
        GoogleSignInOptions gsi = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gsi);
    }

    private void checkGoogleName() {
        FirebaseUser user = mAuth.getCurrentUser();
        String email = mEmail.getText().toString();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (!task.getResult().isEmpty()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String pwd = doc.getString("password");
                                if (pwd != null) {
                                    mPwd.setText(pwd);
                                    Intent intent = new Intent(mContext, UserMainActivity.class);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "checkGoogleName: 비밀번호가 없음");
                                }
                            }
                        }
                    }
                });
    }

    private void emailSignIn(String email, String pwd) {
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener((Activity) mContext, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "emailSignIn: 로그인에 성공하였습니다...");
                        Intent intent = new Intent(mContext, UserMainActivity.class);
                        startActivity(intent);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "onFailure: 이유가 뭘까요??? " + e);
                    startShake(mEmail);
                    startShake(mPwd);
                });
    }
}
