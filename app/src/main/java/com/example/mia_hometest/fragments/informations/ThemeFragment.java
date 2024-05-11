package com.example.mia_hometest.fragments.informations;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.R;
import com.example.mia_hometest.common.ThemeItem;
import com.example.mia_hometest.common.ThemeListAdapter;

import java.util.ArrayList;
import java.util.List;

public class ThemeFragment extends Fragment implements ThemeListAdapter.OnThemeClickListener{
    private final String TAG = ThemeFragment.class.getSimpleName();
    private Context mContext = null;
    private ImageView mGoback = null;
    private ThemeListAdapter mAdapter;
    private List<ThemeItem> mItemList = new ArrayList<>();
    private RecyclerView mRecyclerView;

    public ThemeFragment (Context context) {
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
        Log.d(TAG, " ThemeFragment onCreateView: ");
        View view = inflater.inflate(R.layout.theme, container, false);

        mGoback = view.findViewById(R.id.back);
        mAdapter = new ThemeListAdapter(mContext, this);
        mRecyclerView = view.findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setAdapter(mAdapter);

        mGoback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: 뒤로가기 눌렀다...");
                ((UserMainActivity) getActivity()).goBack();
            }
        });
        setThemeItems();

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onThemeClick(int position) {
        switch (position) {
            case 0:
                Log.d(TAG, "onThemeClick: red");
                break;
            case 1:
                Log.d(TAG, "onThemeClick: orange");
                break;
            case 2:
                Log.d(TAG, "onThemeClick: yellow");
                break;
            case 3:
                break;
            case 4:
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                break;
            default:
                break;
        }
    }

    private void setThemeItems() {
        String[] titles = getResources().getStringArray(R.array.theme_color_title);
        int[] colors = {
                R.color.red,
                R.color.orange,
                R.color.yellow,
                R.color.green,
                R.color.blue,
                R.color.main_p,
                R.color.gray
        };

        for (int i=0; i<titles.length; i++) {
            ThemeItem item = new ThemeItem(titles[i], colors[i]);
            mItemList.add(item);
        }

        mAdapter.setItems(mItemList);
    }
}
