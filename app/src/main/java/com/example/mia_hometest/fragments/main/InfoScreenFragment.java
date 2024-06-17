package com.example.mia_hometest.fragments.main;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.mia_hometest.BaseActivity;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;
import com.example.mia_hometest.fragments.informations.DeleteUserFragment;
import com.example.mia_hometest.fragments.informations.LanguageFragment;
import com.example.mia_hometest.fragments.informations.ThemeFragment;
import com.example.mia_hometest.fragments.informations.UserInfoFragment;
import com.example.mia_hometest.fragments.informations.UserListFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class InfoScreenFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = InfoScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private UserInfoFragment mUserInfo = null;
    private ThemeFragment mTheme = null;
    private UserListFragment mUserList = null;
    private LanguageFragment mLang = null;
    private DeleteUserFragment mDelete = null;
    private AlertDialog.Builder mBuilder;
    private AlertDialog mAlertDialog;

    public InfoScreenFragment (Context context) {
        mContext = context;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " ChartScreenFragment onCreateView: ");
        View view = inflater.inflate(R.layout.info_screen, container, false);
        int textViews[] = {
                R.id.myinfo, R.id.theme, R.id.userlist, R.id.lang, R.id.delete, R.id.logoff
        };

        for (int text : textViews) {
            TextView textView = view.findViewById(text);
            textView.setOnClickListener(this);
        }

        initFragments();
        return view;
    }

    private void initFragments() {
        mUserInfo = new UserInfoFragment(mContext);
        mTheme = new ThemeFragment(mContext);
        mUserList = new UserListFragment(mContext);
        mLang = new LanguageFragment(mContext);
        mDelete = new DeleteUserFragment(mContext);
    }

    private void launchChildFragment (Fragment fragment) {
        ((UserMainActivity) getActivity()).launchFragment(fragment);
    }

    private void showCheckDialog() {
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle("Are you sure you want to delete account?");
        mBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteUser();
            }
        });
        mBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
            }
        });
        mAlertDialog = mBuilder.create();
        mAlertDialog.show();
    }

    private void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore.getInstance().collection("user").whereEqualTo("email", user.getEmail())
                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: 사용자의 정보를 성공적으로 가져왔다면");
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    document.getReference().delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d(TAG, "onSuccess: 디비에서 사용자 정보를 완전히 삭제했다");
                                            user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: 사용자 인증 정보도 삭제 완료..");
                                                        Intent intent = new Intent(mContext, BaseActivity.class);
                                                        intent.putExtra("logoff", true);
                                                        startActivity(intent);
                                                        Toast.makeText(mContext, "Delete account Succeed", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "왜 실패했지??? === " + e);
                                                }
                                            });
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.d(TAG, "디비에서 사용자 정보를 삭제하는것을 실패함 " + e);
                                        }
                                    });
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.myinfo) {
            launchChildFragment(mUserInfo);
        } else if (view.getId() == R.id.theme) {
            launchChildFragment(mTheme);
        } else if (view.getId() == R.id.userlist) {
            launchChildFragment(mUserList);
        } else if (view.getId() == R.id.lang) {
            launchChildFragment(mLang);
        } else if (view.getId() == R.id.logoff) {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(mContext, BaseActivity.class);
            intent.putExtra("logoff", true);
            startActivity(intent);
            Toast.makeText(mContext, "logout Succeed", Toast.LENGTH_SHORT).show();

        } else if (view.getId() == R.id.delete) {
            //정말로 삭제하겠냐는 다이얼로그 표시
            showCheckDialog();
        } else {
            Log.d(TAG, "onClick: do nothing....");
        }
    }
}
