package com.example.mia_hometest.fragments.informations;

import static android.content.Context.MODE_PRIVATE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.BaseActivity;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ThemeItem;
import com.example.mia_hometest.common.ThemeListAdapter;
import com.example.mia_hometest.common.UserNameItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class UserListFragment extends Fragment implements ThemeListAdapter.OnThemeClickListener{
    private final String TAG = UserListFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;
    private ThemeListAdapter<UserNameItem> mAdapter;
    private List<UserNameItem> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private String mCurrentUser;
    private SharedPreferences mPreference;
    private String mBackground;


    public UserListFragment (Context context) {
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
        Log.d(TAG, " UserListFragment onCreateView: ");
        View view = inflater.inflate(R.layout.user_list_screen, container, false);
        mGoback = view.findViewById(R.id.back);
        mAdapter = new ThemeListAdapter<>(mContext, this);
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        getCurrentUserName();
        getAllUserList();

        mGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
                ((UserMainActivity) getActivity()).goBack();
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " UserListFragment 끌게요");
        super.onDestroy();
    }

    @Override
    public void onThemeClick(int position) {
        //바꾸려는 유저의 항목
        UserNameItem userSelect = mItemList.get(position);
        if (!mCurrentUser.isEmpty() && userSelect.getName().equals(mCurrentUser)) {
            Log.d(TAG, "onThemeClick: 현재 로그인한 사용자와 바꾸려는 사용자가 같다면 메소드가 실행되지 않는다");

            Toast.makeText(mContext, "Cannot switch user because current user and selected user are same", Toast.LENGTH_SHORT).show();
        } else {
            switchUser(userSelect.getName());
        }
    }

    private void getCurrentUserName() {
        // FirebaseAuth를 통해 현재 로그인한 유저의 이름을 가져오는 로직
        FirebaseAuth auth = FirebaseAuth.getInstance();
        String currentUserEmail = auth.getCurrentUser().getEmail();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference usersRef = db.collection("user");
        usersRef.whereEqualTo("email", currentUserEmail).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                    mCurrentUser = document.getString("name");
                    Log.d(TAG, "현재 로그인한 유저의 이름: " + mCurrentUser);
                    sendUserPosition();
                }
            }
        });
    }

    private void getAllUserList() {
        Log.d(TAG, "getAllUserList: ");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("user").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "디비에서 정보를 성공적으로 다 긁어왔다면");
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String mUserName = document.getString("name");
                        if (mUserName != null) {
                            mItemList.add(new UserNameItem(mUserName));
                        }
                    }
                    mAdapter.setItems(mItemList);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "디비 정보를 아예 가져오지 못했음 == " + e);
            }
        });
    }

    private void sendUserPosition () {
        if (mCurrentUser != null && !mCurrentUser.isEmpty()) {
            for (int i = 0; i < mItemList.size(); i++) {
                if (mCurrentUser.equals(mItemList.get(i).getName())) {
                    mAdapter.setSelectedItem(i);
                    break;
                }
            }
        }
    }

    private void switchUser(String name) {
        Log.d(TAG, "switchUser:");
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference reference = db.collection("user");
        reference.whereEqualTo("name", name).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                    String email = document.getString("email");
                    String pwd = document.getString("password");

                    if (email!=null && pwd!=null) {
                        FirebaseAuth auth = FirebaseAuth.getInstance();
                        auth.signOut(); //현재 로그인 유저 로그아웃 시킨다
                        auth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: 재로그인 성공");
                                    Toast.makeText(mContext, "switch user succeed", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(mContext, BaseActivity.class);
                                    intent.putExtra("logoff", true);
                                    startActivity(intent);
                                } else {
                                    Log.d(TAG, "onComplete: 재 로그인 실패");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "클릭한 유저로 로그인 실패 == " + e);
                            }
                        });
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "클릭한 유저의 정보 가져오기 실패 :" + e);
            }
        });
    }
}
