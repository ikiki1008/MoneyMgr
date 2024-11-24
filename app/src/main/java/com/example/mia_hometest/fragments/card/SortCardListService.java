package com.example.mia_hometest.fragments.card;

import static java.lang.CharSequence.compare;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.credentials.playservices.CredentialProviderMetadataHolder;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ListItem;
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
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SortCardListService extends Service {
    public String TAG = SortCardListService.class.getSimpleName();
    private String mList;
    private String mType;
    private Context mContext;
    private final Binder mBinder = new LocalBinder();
    private FirebaseFirestore mDb;
    private FirebaseFirestore mStore;
    private String mDate;
    private String mAmount;
    private String mCate;
    private String mAcc;
    private String mNote;
    private Drawable mImage;

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
        mStore = FirebaseFirestore.getInstance();
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
        searchData(mList, mType);
    }

    private void searchData(String list, String type) {
        Log.d(TAG, "searchData: type = " + list + ", date = " + type);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String[] dateRange = getDateRange(list);

        if (user != null) {
            String userId = user.getUid();
            List<ListItem> items = new ArrayList<>();

            if (type.equals("All")) {
                mStore.collection("user").document(userId).collection("income")
                        .whereGreaterThanOrEqualTo("date", dateRange[0])
                        .whereLessThanOrEqualTo("date", dateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                Log.d(TAG, "onComplete: ");
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: 성공");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        mImage = mContext.getDrawable(R.drawable.bunny);
                                        mCate = mContext.getString(R.string.income);
                                        mAmount = documentSnapshot.getString("amount");
                                        mDate = documentSnapshot.getString("date");
                                        String id = documentSnapshot.getId();
                                        mAmount = "+" + mAmount;
                                        items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                        Log.d(TAG, " 최종 리스트 아이템 11" + items);
                                    }
                                    mStore.collection("user").document(userId).collection("outcome")
                                            .whereGreaterThanOrEqualTo("date", dateRange[0])
                                            .whereLessThanOrEqualTo("date", dateRange[1])
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    Log.d(TAG, "onComplete: 22");
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: 성공");
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            mImage = mContext.getDrawable(R.drawable.bunny);
                                                            mCate = documentSnapshot.getString("category");
                                                            mAmount = documentSnapshot.getString("amount");
                                                            mDate = documentSnapshot.getString("date");
                                                            String id = documentSnapshot.getId();
                                                            mAmount = "+" + mAmount;
                                                            items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                                            Log.d(TAG, " 최종 리스트 아이템 22" + items);
                                                        }
                                                    } else {
                                                        Log.d(TAG, "onComplete: 실패");
                                                    }
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d(TAG, "onFailure: 11" + e);
                                                }
                                            });
                                } else {
                                    Log.d(TAG, "onComplete: 실패했다....");
                                }
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.d(TAG, "onFailure: 22 " + e);
                            }
                        });
            } else if (type.equals("income")) {
                mStore.collection("user").document(userId).collection("income")
                        .whereGreaterThanOrEqualTo("date", dateRange[0])
                        .whereLessThanOrEqualTo("date", dateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: 성공");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        mImage = mContext.getDrawable(R.drawable.bunny);
                                        mCate = mContext.getString(R.string.income);
                                        mAmount = documentSnapshot.getString("amount");
                                        mDate = documentSnapshot.getString("date");
                                        String id = documentSnapshot.getId();
                                        mAmount = "+" + mAmount;
                                        items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                    }
                                    Log.d(TAG, "onComplete: 여기는 몇개일까요 " + items);
                                }
                            }
                        });
            } else {
                mStore.collection("user").document(userId).collection("outcome")
                        .whereGreaterThanOrEqualTo("date", dateRange[0])
                        .whereLessThanOrEqualTo("date", dateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: 성공");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        mImage = mContext.getDrawable(R.drawable.bunny);
                                        mCate = documentSnapshot.getString("category");
                                        mAmount = documentSnapshot.getString("amount");
                                        mDate = documentSnapshot.getString("date");
                                        String id = documentSnapshot.getId();
                                        mAmount = "+" + mAmount;
                                        items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                    }
                                    Log.d(TAG, "onComplete: 여기는 몇개일까요 2222" + items);
                                }
                            }
                        });
            }

        }


    }

    private String[] getDateRange(String date) {
        Log.d(TAG, "getDateRange: " + date);
        String startDate = "";
        String endDate = "";
        Calendar calendar = Calendar.getInstance();

        switch (date) {
            case "하루":
            case "daily":
                startDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(new Date());
                endDate = startDate;
                Log.d(TAG, "getDateRange: " + startDate);
                break;
            case "주별":
            case "weekly":
                calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                startDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.DAY_OF_WEEK, 6);
                endDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                Log.d(TAG, "getDateRange: " + startDate + ", " + endDate);
                break;
            case "월별":
            case "Monthly":
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                startDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.MONTH, 1);
                calendar.add(Calendar.DAY_OF_MONTH, -1);
                endDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                Log.d(TAG, "getDateRange: " + startDate + ", " + endDate);
                break;
            case "연별":
            case "Yearly":
                calendar.set(Calendar.DAY_OF_YEAR, 1);
                startDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                calendar.add(Calendar.YEAR, 1);
                calendar.add(Calendar.DAY_OF_YEAR, -1);
                endDate = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault()).format(calendar.getTime());
                Log.d(TAG, "getDateRange: " + startDate + ", " + endDate);
                break;
            default:
                break;
        }
        return new String[]{startDate, endDate};
    }
}

















