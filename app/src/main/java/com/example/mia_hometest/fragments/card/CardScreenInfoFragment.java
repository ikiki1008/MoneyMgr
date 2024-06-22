package com.example.mia_hometest.fragments.card;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.common.CardListAdapter;
import com.example.mia_hometest.fragments.main.CardScreenFragment;
import com.google.firebase.firestore.FirebaseFirestore;

public class CardScreenInfoFragment extends Fragment {
    private Context mContext = null;
    private String mId;
    private final String TAG = CardScreenInfoFragment.class.getSimpleName();
    private ImageView mGoback;

    public CardScreenInfoFragment (Context context, String id ) {
        mContext = context;
        mId = id;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, " CardScreenFragment onCreateView: ");
        View view = inflater.inflate(R.layout.card_specific_screen, container, false);
        mGoback = view.findViewById(R.id.back);
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
        super.onDestroy();
        Log.d(TAG, "onDestroy: ");
    }

}
