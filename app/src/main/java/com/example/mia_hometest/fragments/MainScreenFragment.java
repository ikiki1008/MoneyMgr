package com.example.mia_hometest.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.common.TestGridAdapter;

public class MainScreenFragment extends Fragment {
    private final String TAG = MainScreenFragment.class.getSimpleName();
    private Context mContext = null;
    private RecyclerView mRecyclerView;
    private TestGridAdapter mAdapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.d(TAG, "onAttach: ");
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.main_screen, container, false);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, " MainScreenFragment 끌게요");
        super.onDestroy();
    }

    public MainScreenFragment (Context context) {
        mContext = context;
    }


}
