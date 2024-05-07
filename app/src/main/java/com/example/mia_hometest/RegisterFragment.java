package com.example.mia_hometest;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class RegisterFragment extends Fragment {
    private final String TAG = RegisterFragment.class.getSimpleName();
    private SharedPreferences mSharedPreference;
    private static final Boolean mValue = true;
    private Context mContext = null;
    private TextView mCancel = null;
    private TextView mSave = null;
    private EditText mEmail = null;
    private EditText mName = null;
    private EditText mPass = null;
    private ImageView mMoneyImg = null;
    private FirebaseAuth mAuth;
    private SignInButton mGoogleRegister = null;
    private ActivityResultLauncher<Intent> activityResultLauncher;
    private GoogleSignInClient mGoogleSignInClient;

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

        mGoogleRegister = view.findViewById(R.id.google_login_btn);
        mSave = view.findViewById(R.id.signBtn);
        mCancel = view.findViewById(R.id.cancelBtn);
        mEmail = view.findViewById(R.id.set_email);
        mName = view.findViewById(R.id.set_name);
        mPass = view.findViewById(R.id.set_pwd);
        mMoneyImg = view.findViewById(R.id.moneyImg);
        GlideDrawableImageViewTarget gif = new GlideDrawableImageViewTarget(mMoneyImg); //gif setting
        Glide.with(mContext).load(R.drawable.money_stack).into(gif);
        googleSignInit();

        mSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String name = mName.getText().toString();
                String pwd = mPass.getText().toString();

                if (email.isEmpty()) {
                    Log.d(TAG, " ##### you cannot login without typing yout id on textbox ");
                    startShake(mEmail);
                }
                else if (name.isEmpty()) {
                   startShake(mName);
                }
                else if (pwd.isEmpty()) {
                    startShake(mPass);
                }
                else {
                    checkDoubleComps();
                }
            }
        });

        mGoogleRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                googleSignIn();
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

    @Override
    public void onDestroy() {
        Log.d(TAG, " LoginFragment 끌게요");
        super.onDestroy();
    }

    private void startShake(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        editText.startAnimation(shake);
    }

    private void checkDoubleComps() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String email = mEmail.getText().toString();
        String name = mName.getText().toString();

        db.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {
                        Log.d(TAG, "checkDoubleComps: 요소가 하나라도 중복되는게 있습니다...");
                        startShake(mEmail);
                    }
                    else {
                        Log.d(TAG, "checkDoubleComps 중복되는 이메일이 없다..");
                        db.collection("user")
                                .whereEqualTo("name", name)
                                .get()
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful() && !task1.getResult().isEmpty()) {
                                        startShake(mName);
                                        Log.d(TAG, "checkDoubleComps: 이름이 중복된다");
                                    } else {
                                        Log.d(TAG, "checkDoubleComps: 최종적으로 등록할 수 있음.");
                                        addNewData();
                                    }
                                });
                    }
                });
    }

    private void addNewData() {
        Log.d(TAG, " ##### put new data resource into the db");
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> user = new HashMap<>();
        user.put("email", mEmail.getText().toString());
        user.put("name", mName.getText().toString());
        user.put("password", mPass.getText().toString());

        db.collection("user")
                .add(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "addNewData: 데이터를 추가하였음");
                        registerFirebaseAuth(mEmail.getText().toString(), mPass.getText().toString());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "addNewData: failed to add new data....");
                });

    }

    private void registerFirebaseAuth (String email, String pwd) {
        Log.d(TAG, "registerFirebaseAuth: ");

        mAuth.createUserWithEmailAndPassword(email, pwd)
                .addOnCompleteListener((Activity) mContext, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "registerFirebaseAuth: 로그인 등록 완료");
                        loginWAuth(email, pwd);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.d(TAG, "registerFirebaseAuth: 왜 실패했지... " + e);
                });
    }

    private void loginWAuth (String email, String pwd) {
        Log.d(TAG, "loginWAuth: ");
        mAuth.signInWithEmailAndPassword(email, pwd)
                .addOnCompleteListener((Activity) mContext, task -> {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "loginWAuth: 가입 완료 후 로그인 성공");
                        Intent intent = new Intent(mContext, UserMainActivity.class);
                        startActivity(intent);
                    }
                });
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
                                Log.d(TAG, " 뭔가 또 실패... " + e);
                                throw new RuntimeException(e);
                            }
                        }
                    }
                });
        configSignIn();
        mAuth = FirebaseAuth.getInstance();
    }

    private void firebaseAuthGoogle (GoogleSignInAccount account) {
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
                        } else {
                            Log.d(TAG, " #### 로그인 실패...");
                        }
                        return;
                    }
                });
    }

    private void configSignIn() {
        Log.d(TAG, "configSignIn: ");
        GoogleSignInOptions gsi = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(mContext, gsi);
    }

    private void googleSignIn() {
        Log.d(TAG, "googleSignIn: ");
        Intent intent = mGoogleSignInClient.getSignInIntent();
        activityResultLauncher.launch(intent);
    }

}
