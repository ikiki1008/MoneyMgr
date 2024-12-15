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
import androidx.core.content.ContextCompat;
import androidx.credentials.playservices.CredentialProviderMetadataHolder;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.CardListAdapter;
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
    private String[] mDateRange;
    private final Binder mBinder = new LocalBinder();
    private FirebaseFirestore mDb;
    private FirebaseFirestore mStore;
    private String mDate;
    private String mAmount;
    private String mCate;
    private String mAcc;
    private String mNote;
    private Drawable mImage;
    private CardListAdapter mAdapter;
    private List<ListItem> itemList = new ArrayList<>();

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
        mAdapter = new CardListAdapter(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }


    //조회해야할 날짜, 리스트 타입, income 혹은 outcome 타입 총 3가지를 확인해야 한다...
    public void getListString (String date, String list, String type) {
        mList = list;
        mType = type;
        Log.d(TAG, "list ==" + mList + ", type ==" + type + " date == " + date);
        mDateRange = getDateRange(date, list);
        searchData(mList, mType);
    }

    private void searchData(String list, String type) {
        Log.d(TAG, "searchData: type = " + list + ", date = " + type);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        //String[] dateRange = getDateRange(list);
        List<ListItem> items = new ArrayList<>();

        if (user != null) {
            String userId = user.getUid();

            if (type.equals("All")) {
                mStore.collection("user").document(userId).collection("income")
                        .whereGreaterThanOrEqualTo("date", mDateRange[0])
                        .whereLessThanOrEqualTo("date", mDateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
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
                                        Collections.sort(items, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                                        setItems(items);
                                    }
                                    mStore.collection("user").document(userId).collection("outcome")
                                            .whereGreaterThanOrEqualTo("date", mDateRange[0])
                                            .whereLessThanOrEqualTo("date", mDateRange[1])
                                            .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                    Log.d(TAG, "onComplete: 22");
                                                    if (task.isSuccessful()) {
                                                        Log.d(TAG, "onComplete: 성공");
                                                        for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                            mCate = documentSnapshot.getString("category");
                                                            getImage(mCate);
                                                            mAmount = documentSnapshot.getString("amount");
                                                            mDate = documentSnapshot.getString("date");
                                                            String id = documentSnapshot.getId();
                                                            mAmount = "-" + mAmount;

                                                            items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                                            Log.d(TAG, " 최종 리스트 아이템 22" + items);
                                                            Collections.sort(items, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                                                            setItems(items);
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
                        .whereGreaterThanOrEqualTo("date", mDateRange[0])
                        .whereLessThanOrEqualTo("date", mDateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
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
                                        Collections.sort(items, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                                        setItems(items);
                                    }
                                    Log.d(TAG, "onComplete: 여기는 몇개일까요 " + items);
                                }
                            }
                        });
            } else {
                mStore.collection("user").document(userId).collection("outcome")
                        .whereGreaterThanOrEqualTo("date", mDateRange[0])
                        .whereLessThanOrEqualTo("date", mDateRange[1])
                        .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @SuppressLint("UseCompatLoadingForDrawables")
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    Log.d(TAG, "onComplete: 성공");
                                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                        mCate = documentSnapshot.getString("category");
                                        getImage(mCate);
                                        mAmount = documentSnapshot.getString("amount");
                                        mDate = documentSnapshot.getString("date");
                                        String id = documentSnapshot.getId();
                                        mAmount = "-" + mAmount;

                                        items.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                        Collections.sort(items, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                                        setItems(items);
                                    }
                                    Log.d(TAG, "onComplete: 여기는 몇개일까요 2222" + items);
                                }
                            }
                        });
            }
        }
    }

    private int compare (String date1, String date2) {
        return date2.compareTo(date1);
    }

    private void getImage(String category) {
        if (category.equals(mContext.getString(R.string.shopping))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.shopper);
        } else if (category.equals(mContext.getString(R.string.hospital))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.hospital);
        } else if (category.equals(mContext.getString(R.string.food))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.donut);
        } else if (category.equals(mContext.getString(R.string.rent))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.house_fee);
        } else if (category.equals(mContext.getString(R.string.phone))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.mobile_text);
        } else if (category.equals(mContext.getString(R.string.card))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.shopping);
        } else if (category.equals(mContext.getString(R.string.social))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.confetti);
        } else if (category.equals(mContext.getString(R.string.hobby))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.artist);
        } else if (category.equals(mContext.getString(R.string.ott))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.netflix);
        } else if (category.equals(mContext.getString(R.string.household))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.paperroll);
        } else if (category.equals(mContext.getString(R.string.trans))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.vehicles);
        } else if (category.equals(mContext.getString(R.string.sports))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.physical);
        } else if (category.equals(mContext.getString(R.string.loan))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.tax);
        } else if (category.equals(mContext.getString(R.string.edu))) {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.book);
        } else {
            mImage = ContextCompat.getDrawable(mContext, R.drawable.options);
        }
    }

    public void setItems(List<ListItem> list) {
        this.itemList = list;
    }

    public List<ListItem> getItems() {
        return itemList;
    }

    private String[] getDateRange(String date, String type) {
        Log.d(TAG, "getDateRange: " + date + ", type: " + type);
        String startDate;
        String endDate;
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd", Locale.getDefault());

        try {
            // 날짜 포맷 파싱
            if (date.matches("\\d{4}")) { // yyyy
                calendar.set(Integer.parseInt(date), Calendar.JANUARY, 1); // 연도의 첫날
            } else if (date.matches("\\d{4}/\\d{2}")) { // yyyy/MM
                String[] parts = date.split("/");
                calendar.set(Integer.parseInt(parts[0]), Integer.parseInt(parts[1]) - 1, 1); // 연도와 월의 첫날
            } else if (date.matches("\\d{4}/\\d{2}/\\d{2}")) { // yyyy/MM/dd
                Date parsedDate = sdf.parse(date);
                calendar.setTime(parsedDate); // 정확한 날짜 설정
            } else {
                throw new IllegalArgumentException("Invalid date format");
            }

            // 시작 날짜 초기화
            startDate = sdf.format(calendar.getTime());

            // 범위 계산
            switch (type) {
                case "하루":
                case "daily":
                    endDate = startDate; // 하루 단위는 시작과 종료가 동일
                    break;

                case "주별":
                case "weekly":
                    // 현재 주의 시작 (기본: 월요일 시작)
                    calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
                    startDate = sdf.format(calendar.getTime());

                    // 현재 주의 끝
                    calendar.add(Calendar.DAY_OF_WEEK, 6);
                    endDate = sdf.format(calendar.getTime());
                    break;

                case "월별":
                case "Monthly":
                    // 현재 달의 시작
                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                    startDate = sdf.format(calendar.getTime());

                    // 현재 달의 마지막 날
                    calendar.add(Calendar.MONTH, 1);
                    calendar.add(Calendar.DAY_OF_MONTH, -1);
                    endDate = sdf.format(calendar.getTime());
                    break;

                case "연별":
                case "Yearly":
                    // 연도의 첫날
                    calendar.set(Calendar.DAY_OF_YEAR, 1);
                    startDate = sdf.format(calendar.getTime());

                    // 연도의 마지막 날
                    calendar.add(Calendar.YEAR, 1);
                    calendar.add(Calendar.DAY_OF_YEAR, -1);
                    endDate = sdf.format(calendar.getTime());
                    break;

                default:
                    startDate = "";
                    endDate = "";
                    Log.e(TAG, " ??? Invalid type provided: " + type);
                    break;
            }

            Log.d(TAG, "getDateRange result: StartDate=" + startDate + ", EndDate=" + endDate);
            return new String[]{startDate, endDate};

        } catch (Exception e) {
            Log.e(TAG, "Error parsing date or calculating range: " + e.getMessage());
            return new String[]{"", ""}; // 잘못된 입력값 처리
        }
    }
}

















