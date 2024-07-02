package com.example.mia_hometest.fragments.main;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.util.TypedValue;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.common.ListItem;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.common.SpecificListItem;
import com.example.mia_hometest.fragments.card.CardScreenInfoFragment;
import com.example.mia_hometest.fragments.card.SortCardListService;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CardScreenFragment extends Fragment implements View.OnClickListener, CardListAdapter.OnSwipeListener, CardListAdapter.OnListClickListener {
    private final String TAG = CardScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private TextView mAll = null;
    private TextView mExpense = null;
    private TextView mIncome = null;
    private TextView mListTitle = null;
    private ImageView mBtn = null;
    private String[] mlist;
    private String mDate;
    private String mAmount;
    private String mCate;
    private String mAcc;
    private String mNote;
    private String mTrans;
    private Drawable mImage;
    private AlertDialog.Builder mBuilder;
    private RecyclerView mRecyclerView;
    private CardListAdapter mAdapter;
    private FirebaseFirestore mStore;
    private CardScreenInfoFragment mCardScreenInfoFragment = null;
    private AlertDialog mAlertDialog;
    private SortCardListService mService;
    private boolean mIsServiceOn = false;
    private boolean mIsOutcome = false;
    private boolean mIsAll = true;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            SortCardListService.LocalBinder binder = (SortCardListService.LocalBinder) iBinder;
            mService = binder.getService();
            mIsServiceOn = true;
            if (!mListTitle.getText().equals("") && mIsAll) {
                mIsOutcome = false;
                mService.getListString(mListTitle.getText().toString(), "All");
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mService = null;
            mIsServiceOn = false;
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " CardScreenFragment onCreateView: ");
        View view = inflater.inflate(R.layout.expense_all_screen, container, false);

        mStore = FirebaseFirestore.getInstance();
        mAll = view.findViewById(R.id.all);
        mExpense = view.findViewById(R.id.expense);
        mIncome = view.findViewById(R.id.income);
        mBtn = view.findViewById(R.id.listBtn);
        mListTitle = view.findViewById(R.id.listText);

        mAdapter = new CardListAdapter(mContext);
        mAdapter.setSwipeListener(this);
        mAdapter.setListClickListener(this);
        mRecyclerView = view.findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mAll.setOnClickListener(this);
        mIncome.setOnClickListener(this);
        mExpense.setOnClickListener(this);
        mBtn.setOnClickListener(this);


        mListTitle.setText(R.string.list_week);
        int themeColor = getThemeColor(android.R.attr.textColor);
        mAll.setTextColor(themeColor);
        fetchData("all");
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: ");
        Intent intent = new Intent(mContext, SortCardListService.class); //service class connect!
        mContext.bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause: ");
        if (mIsServiceOn) {
            Log.d(TAG, "onDestroy: 서비스 끄기``");
            mContext.unbindService(mConnection);
            mIsServiceOn = false;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " CardScreenFragment 끌게요");
        super.onDestroy();
    }

    public CardScreenFragment (Context context) {
        mContext = context;
    }

    public interface OnListItemClick {
        void onItemClick (String item);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    public void fetchData(String type) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (type.equals("all")) {
            if (user != null) {
                String userId = user.getUid();
                List<ListItem> listItems = new ArrayList<>();

                // Fetch income data
                mStore.collection("user").document(userId).collection("income")
                        .get().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                    mImage = mContext.getDrawable(R.drawable.bunny);
                                    mCate = mContext.getString(R.string.income);
                                    mAmount = documentSnapshot.getString("amount");
                                    mDate = documentSnapshot.getString("date");
                                    String id = documentSnapshot.getId();
                                    mAmount = "+" + mAmount;
                                    listItems.add(new ListItem(mImage, id, mCate, mAmount, mDate));
                                }

                                // After fetching all income data, fetch outcome data
                                mStore.collection("user").document(userId).collection("outcome")
                                        .get().addOnCompleteListener(outcomeTask -> {
                                            if (outcomeTask.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : outcomeTask.getResult()) {
                                                    mCate = documentSnapshot.getString("category");
                                                    getImage(mCate);
                                                    mAmount = documentSnapshot.getString("amount");
                                                    mDate = documentSnapshot.getString("date");
                                                    String id = documentSnapshot.getId();
                                                    mAmount = "-" + mAmount;
                                                    listItems.add(new ListItem(mImage,id, mCate, mAmount, mDate));
                                                }

                                                // Sort listItems by date in descending order
                                                Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));

                                                // Set items to adapter
                                                mAdapter.setItems(listItems);
                                            }
                                        });
                            }
                        });
            }
        } else if (type.equals("income")) {
            String userId = user.getUid();
            mStore.collection("user").document(userId).collection("income")
                    .get().addOnCompleteListener(task -> {
                        List<ListItem> listItems = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                Drawable image = mContext.getDrawable(R.drawable.bunny);
                                String cate = mContext.getString(R.string.income);
                                String amount = documentSnapshot.getString("amount");
                                String date = documentSnapshot.getString("date");
                                String id = documentSnapshot.getId();
                                amount = "+" + amount;
                                listItems.add(new ListItem(image,id, cate, amount, date));
                            }
                        }
                        Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                        mAdapter.setItems(listItems);
                    });
        } else {
            String userId = user.getUid();
            mStore.collection("user").document(userId).collection("outcome")
                    .get().addOnCompleteListener(task -> {
                        List<ListItem> listItems = new ArrayList<>();
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                String cate = documentSnapshot.getString("category");
                                getImage(cate);
                                Drawable image = mImage;
                                String amount = documentSnapshot.getString("amount");
                                String date = documentSnapshot.getString("date");
                                String id = documentSnapshot.getId();
                                amount = "-" + amount;
                                listItems.add(new ListItem(image, id, cate, amount, date));
                            }
                        }
                        Collections.sort(listItems, (item1, item2) -> compare(item1.getDate(), item2.getDate()));
                        mAdapter.setItems(listItems);
                    });
        }
    }

    private int compare (String date1, String date2) {
        return date2.compareTo(date1);
    }

    private int getThemeColor(int attr) {
        TypedValue typedValue = new TypedValue();
        if (mContext.getTheme().resolveAttribute(attr, typedValue, true)) {
            return typedValue.data;
        } else {
            throw new IllegalArgumentException("Attribute not found in theme");
        }
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

    private void showList(final OnListItemClick listener) {
        mlist = getResources().getStringArray(R.array.list_dialog);
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setItems(mlist, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String item = mlist[i];
                listener.onItemClick(item);
            }
        });
        AlertDialog alertDialog = mBuilder.create();
        alertDialog.show();
    }

    /*
     * CardScreenInfoFragment 의 인스턴스를 두개로 나눈다. income 이냐 expense 냐에 따라 카테고리가 인자로 쓰일지 결정되는 부분이기 떄문이다.
     * */
    private void startSpecificFragment(String transactions, String docId) {
        if (transactions.equals(mContext.getString(R.string.income))) {
            CardScreenInfoFragment fragment = CardScreenInfoFragment.newInstance(mTrans, docId, mDate, mAmount, mAcc, mNote);
            ((UserMainActivity) getActivity()).launchFragment(fragment);
        } else if (transactions.equals(mContext.getString(R.string.expense))){
            CardScreenInfoFragment fragment = CardScreenInfoFragment.newInstance(mTrans, docId, mDate, mAmount, mCate, mAcc, mNote);
            ((UserMainActivity) getActivity()).launchFragment(fragment);
        }
    }

    private void showWarning (int position) {
        mBuilder = new AlertDialog.Builder(mContext);
        mBuilder.setTitle(mContext.getString(R.string.warn_list));
        mBuilder.setPositiveButton(mContext.getString(R.string.answer_yes), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Log.d(TAG, "onClick: 해당 정보 삭제합니다");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    ListItem itemToDelete = mAdapter.getItems().get(position);

                    Task<Void> deleteIncomeTask = mStore.collection("user").document(userId).collection("income").document(itemToDelete.getId())
                            .delete();

                    Task<Void> deleteOutcomeTask = mStore.collection("user").document(userId).collection("outcome").document(itemToDelete.getId())
                            .delete();

                    // 먼저 income에서 삭제 시도
                    deleteIncomeTask.addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "DocumentSnapshot successfully deleted from income!");
                        mAdapter.getItems().remove(position);
                        mAdapter.notifyItemRemoved(position);
                        mAdapter.notifyItemRangeChanged(position, mAdapter.getItems().size());
                    }).addOnFailureListener(e -> {
                        // income에서 실패하면 outcome에서 삭제 시도
                        Log.w(TAG, "Error deleting document from income, trying outcome", e);
                        deleteOutcomeTask.addOnSuccessListener(aVoid -> {
                            Log.d(TAG, "DocumentSnapshot successfully deleted from outcome!");
                            mAdapter.getItems().remove(position);
                            mAdapter.notifyItemRemoved(position);
                            mAdapter.notifyItemRangeChanged(position, mAdapter.getItems().size());
                        }).addOnFailureListener(e2 -> Log.w(TAG, "Error deleting document from outcome", e2));
                    });
                }
            }
        });
        mBuilder.setNegativeButton(mContext.getString(R.string.answer_no), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (mAlertDialog != null) {
                    mAlertDialog.cancel();
                }
            }
        });
        mAlertDialog = mBuilder.create();
        mBuilder.show();
    }

    @Override
    public void onClick(View view) {
        int themeColor = getThemeColor(android.R.attr.textColor);
        int secondColor = getThemeColor(android.R.attr.textColorSecondary);

        switch (view.getId()) {
            case R.id.all:
                Log.d(TAG, "onClick: all");
                mIsAll = true;
                mIsOutcome = false;
                fetchData("all");
                mAll.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "All");
                break;
            case R.id.expense:
                Log.d(TAG, "onClick: expense");
                mIsAll = false;
                mIsOutcome = true;
                fetchData("outcome");
                mExpense.setTextColor(themeColor);
                mAll.setTextColor(secondColor);
                mIncome.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "outcome");
                break;
            case R.id.income:
                Log.d(TAG, "onClick: income");
                mIsAll = false;
                mIsOutcome = false;
                fetchData("income");
                mIncome.setTextColor(themeColor);
                mExpense.setTextColor(secondColor);
                mAll.setTextColor(secondColor);
                mService.getListString(mListTitle.getText().toString(), "income");
                break;
            case R.id.listBtn:
                showList(new OnListItemClick() {
                    @Override
                    public void onItemClick(String item) {
                        Log.d(TAG, "onItemClick: " + item);
                        if (item != null && !item.equals("")) {
                            mListTitle.setText(item);
                            if (mIsAll) {
                                mService.getListString(mListTitle.getText().toString(), "All");
                            } else if (!mIsAll && mIsOutcome) {
                                mService.getListString(mListTitle.getText().toString(), "outcome");
                            } else if (!mIsAll && !mIsOutcome) {
                                mService.getListString(mListTitle.getText().toString(), "income");
                            }
                        } else {
                            mAlertDialog.dismiss();
                        }
                    }
                });
                break;
            default:
                Log.d(TAG, "onClick: 랄라라라라 디폴트");
                break;
        }
    }

    @Override
    public void onSwipeLeft(int position) {
        Log.d(TAG, "onSwipeLeft: 왼쪽으로 스와이핑 했습니다.");
        showWarning(position);
    }

    @Override
    public void onListClick(int position) {
        Log.d(TAG, "onListClick: ");
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            ListItem item = mAdapter.getItems().get(position);
            String itemId = item.getId();
            Log.d(TAG, "onSwipeLeft: 아이템 아이디는? " + itemId);

            mStore.collection("user").document(userId).collection("income").document(itemId).get()
                    .addOnCompleteListener(task -> {
                        Log.d(TAG, "onSwipeLeft: income onComplete");
                        if (task.isSuccessful()) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                mDate = documentSnapshot.getString("date");
                                mAmount = documentSnapshot.getString("amount");
                                mAcc = documentSnapshot.getString("account");
                                mNote = documentSnapshot.getString("note");
                                mTrans = mContext.getString(R.string.income);
                                if (mNote == null) {
                                    mNote = "No Description";
                                }
                                startSpecificFragment(mTrans, itemId);
                            } else {
                                Log.d(TAG, "onSwipeLeft: 인컴에 없음");
                                mStore.collection("user").document(userId).collection("outcome").document(itemId).get()
                                        .addOnCompleteListener(outcomeTask -> {
                                            Log.d(TAG, "onSwipeLeft: outcome onComplete");
                                            if (outcomeTask.isSuccessful()) {
                                                Log.d(TAG, "onSwipeLeft: outcome success");
                                                DocumentSnapshot documentSnapshot1 = outcomeTask.getResult();
                                                if (documentSnapshot1.exists()) {
                                                    mDate = documentSnapshot1.getString("date");
                                                    mAmount = documentSnapshot1.getString("amount");
                                                    mAcc = documentSnapshot1.getString("account");
                                                    mCate = documentSnapshot1.getString("category");
                                                    mNote = documentSnapshot1.getString("note");
                                                    mTrans = mContext.getString(R.string.expense);
                                                    if (mNote == null) {
                                                        mNote = "No Description";
                                                    }
                                                    Log.d(TAG, "onSwipeLeft: 도큐먼트가 있다면 ... " + mDate + mAmount + mAcc + mCate + mNote + mTrans);
                                                    startSpecificFragment(mTrans, itemId);
                                                } else {
                                                    Log.d(TAG, "onSwipeLeft: wtf111");
                                                }
                                            } else {
                                                Log.d(TAG, "onSwipeLeft: wtf 22222");
                                            }
                                        });
                            }
                        }
                    });
        }
    }
}
