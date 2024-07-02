package com.example.mia_hometest.fragments.card;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.playservices.CredentialProviderMetadataHolder;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class SortCardListService extends Service {
    public String TAG = SortCardListService.class.getSimpleName();
    private String mList;
    private String mType;
    private Context mContext;
    private final Binder mBinder = new LocalBinder();
    private FirebaseFirestore mDb;
    private OnFetchListListener mListener;

    public interface OnFetchListListener {
        void onDataFetch(ArrayList<String> datelist);
    }

    public void setOnFetchListener (OnFetchListListener listener) {
        mListener = listener;
    }

    public class LocalBinder extends Binder {
        public SortCardListService getService() {
            return SortCardListService.this;
        }
    }
    
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }
    
    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: ");
        super.onCreate();
        mContext = getApplicationContext();
        mDb = FirebaseFirestore.getInstance();
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

    public void getListString (String list, String type) {
        mList = list;
        mType = type;
        Log.d(TAG, "list ==" + mList + ", type ==" + type);
    }

    private void searchData(String type) {
        Log.d(TAG, "searchData: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

        }
    }
}

















