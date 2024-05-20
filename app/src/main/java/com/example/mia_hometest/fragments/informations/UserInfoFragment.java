package com.example.mia_hometest.fragments.informations;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.mia_hometest.BaseActivity;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class UserInfoFragment extends Fragment implements View.OnClickListener{
    private final String TAG = UserInfoFragment.class.getSimpleName();
    private static final int REQUEST_IMAGE_PICK = 1001;
    private Context mContext = null;
    private ImageView mGoback = null;
    private ImageView mIcon = null;
    private ImageView mLock = null;
    private TextView mEdit = null;
    private TextView mSave = null;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private ArrayList<EditText> mEditTexts = new ArrayList<>();
    private boolean mIsEditing = false;

    public UserInfoFragment (Context context) {
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
        Log.d(TAG, " UserInfoFragment onCreateView: ");
        View view = inflater.inflate(R.layout.user_info, container, false);
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mGoback = view.findViewById(R.id.back);
        mIcon = view.findViewById(R.id.icon);
        mLock = view.findViewById(R.id.lockImg);
        mEdit = view.findViewById(R.id.edit);
        mSave = view.findViewById(R.id.save);

        mGoback.setOnClickListener(this);
        mIcon.setOnClickListener(this);
        mLock.setOnClickListener(this);
        mEdit.setOnClickListener(this);
        mSave.setOnClickListener(this);
        
        int[] infos = {
                R.id.name, R.id.email, R.id.pwd
        };
        for (int info : infos) {
            EditText editText = view.findViewById(info);
            editText.setOnClickListener(this);
            mEditTexts.add(editText); //put values of editTexts into arrayList to use save / edit functions easy.
        }

        mEditTexts.get(1).setText(mAuth.getCurrentUser().getEmail());
        String email = mEditTexts.get(1).getText().toString();

        mDb.collection("user")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            QueryDocumentSnapshot document = (QueryDocumentSnapshot) task.getResult().getDocuments().get(0);
                            String name = document.getString("name");
                            String password = document.getString("password");
                            mEditTexts.get(0).setText(name);
                            mEditTexts.get(2).setText(password);
                            mEditTexts.get(2).setTransformationMethod(PasswordTransformationMethod.getInstance());
                        }
                    }
                });
        getPfp(); //set pfp

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getPfp() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        storageReference.child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg").getDownloadUrl().addOnSuccessListener(
                new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Glide.with(mContext)
                                .load(uri)
                                .into(mIcon);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        mIcon.setImageResource(R.drawable.user);
                    }
                });
    }

    private void startShake(EditText editText) {
        Animation shake = AnimationUtils.loadAnimation(mContext, R.anim.shake);
        editText.startAnimation(shake);
    }

    private void updateDb() { //update the infos of database
        Log.d(TAG, "updateDb: ");
        String newName = mEditTexts.get(0).getText().toString();
        String newPwd = mEditTexts.get(2).getText().toString();
        String email = mEditTexts.get(1).getText().toString();

        if (!email.isEmpty()) {
            FirebaseFirestore.getInstance().collection("user")
                    .whereEqualTo("email", email)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()){
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    Log.d(TAG, "updateDb: 사용자 문서를 찾았습니다: " + document.getId());
                                    document.getReference().update(
                                            "name", newName,
                                            "password", newPwd)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d(TAG, "onSuccess: 업데이트 성공...");
                                                    updateAuth(newName, newPwd);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: 왜 안됐는지 나도 모름 .. " + e);
                                                }
                                            });
                                }
                            }
                        }
                    });
        }

    }

    private void updateAuth(String newName, String newPwd) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        UserProfileChangeRequest request = new UserProfileChangeRequest.Builder()
                .setDisplayName(newName).build();
        user.updateProfile(request).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.d(TAG, "onComplete: 일단 auth 의 이름은 바꿨음");
                    user.updatePassword(newPwd).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Log.d(TAG, "onComplete: 이제 모두 성공적으로 다 변경했다...");
                                mEditTexts.get(0).setText(newName);
                                mEditTexts.get(2).setText(newPwd);
                                mEditTexts.get(0).setEnabled(false);
                                mEditTexts.get(2).setEnabled(false);
                                mIsEditing = false;
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: 이건 왜 실패했을까용용 " + e);
                        }
                    });
                }
            }
        });
    }

    private void chooseProfilePic() {
        Log.d(TAG, "setProfilePic: get profile picture from gallery");
        Intent intentPic = new Intent(Intent.ACTION_PICK);
        intentPic.setType("image/*");
        startActivityForResult(intentPic, REQUEST_IMAGE_PICK);
    }

    private void updateProfilePic(String image) {
        Log.d(TAG, "updateProfilePic: ");
        //set picture url on user profile in firebase
        FirebaseUser user = mAuth.getCurrentUser();
        UserProfileChangeRequest update = new UserProfileChangeRequest.Builder()
                .setPhotoUri(Uri.parse(image))
                .build();

        if (user != null) { // null check
            user.updateProfile(update)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "성공적으로 프사가 업댓됨");
                        } else {
                            Log.d(TAG, "task failed...");
                        }
                    });
        }
    }

    @Override
    public void onActivityResult (int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: ");
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK) {
            Log.d(TAG, "onActivityResult: 됐나???");
            if (data != null && data.getData() != null) {
                Log.d(TAG, "이미지를 들고왔다");
                Uri uri = data.getData();
                mIcon.setImageURI(uri); //set profile pic on imageView

                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference storageReference = storage.getReference().child("profile_images/" + mAuth.getCurrentUser().getUid() + ".jpg");
                storageReference.putFile(uri)
                        .addOnSuccessListener(taskSnapshot -> { //update picture on storage
                            storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                                Log.d(TAG, "이미지를 스토리지에 옮기는데 성공");
                                String image = uri1.toString();
                                updateProfilePic(image);
                            });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(mContext, "이미지를 스토리지에 옮기는데 실패", Toast.LENGTH_SHORT).show();
                        });
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (mIsEditing) {
                    Toast.makeText(mContext, "수정중인 정보를 저장해 주세요.", Toast.LENGTH_SHORT).show();
                } else {
                    ((UserMainActivity) getActivity()).goBack();
                }
                break;
            case R.id.icon:
                chooseProfilePic();
                break;
            case R.id.lockImg:
                Log.d(TAG, "onClick: 이미지 클릭했다");
                if (mEditTexts.get(2).getTransformationMethod() == PasswordTransformationMethod.getInstance()) {
                    mEditTexts.get(2).setTransformationMethod(null);
                    mLock.setImageResource(R.drawable.unlock);
                    Log.d(TAG, "onClick: 이미지 잠금 해제");
                } else {
                    mEditTexts.get(2).setTransformationMethod(PasswordTransformationMethod.getInstance());
                    mLock.setImageResource(R.drawable.lock);
                    Log.d(TAG, "onClick: 이미지 잠금 모드");
                }
                break;
            case R.id.edit:
                if (mIsEditing) {
                    Log.d(TAG, "onClick: 반복 수정 클릭 금지...");
                } else {
                    Log.d(TAG, "onClick: 수정 모드 시작");
                    mEditTexts.get(0).setEnabled(true);
                    mEditTexts.get(2).setEnabled(true);
                    mIsEditing = true;
                }
                break;
            case R.id.save:
                if (mIsEditing) {
                    Log.d(TAG, "onClick: 업데이트 시작");
                    boolean anyEmpty = false;
                    for (EditText editText : mEditTexts) {
                        if (editText.getText().toString().isEmpty()) {
                            Log.d(TAG, "onClick: 정보가 하나라도 비어있음");
                            anyEmpty = true;
                            break;
                        }
                    }
                    if (anyEmpty) {
                        Toast.makeText(mContext, "모든 정보를 입력하세요", Toast.LENGTH_SHORT).show();
                    } else {
                        updateDb();
                    }
                }
                break;
            default:
                break;
        }
    }
}
