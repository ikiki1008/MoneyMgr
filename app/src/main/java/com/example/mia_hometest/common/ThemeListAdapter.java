package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;

import java.util.ArrayList;
import java.util.List;

public class ThemeListAdapter<T extends DisplayItem> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ThemeListAdapter.class.getSimpleName();
    private final Context mContext;
    private final int mColumnCount;
    private final LayoutInflater mInflater;
    private static final int TILE_VIEW_TYPE = 1;
    private final ThemeListSizeLookup mThemeLookup = new ThemeListSizeLookup();
    private List<T> mItems = new ArrayList<>();
    private OnThemeClickListener mListener;
    private int mSelectedItem = RecyclerView.NO_POSITION; // 선택된 아이템의 위치를 저장하는 변수
    private SharedPreferences mPrefs;
    private static final String PREF_SELECTED_ITEM = "selected_item";

    // 생성자
    public ThemeListAdapter(Context context, OnThemeClickListener listener) {
        mContext = context;
        mColumnCount = 1;
        mListener = listener;
        mInflater = LayoutInflater.from(context);
        mPrefs = context.getSharedPreferences("theme_prefs", Context.MODE_PRIVATE);
        mSelectedItem = mPrefs.getInt(PREF_SELECTED_ITEM, RecyclerView.NO_POSITION);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeViewHolder(mInflater.inflate(R.layout.theme_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        T item = mItems.get(position);
        ThemeViewHolder viewHolder = (ThemeViewHolder) holder;
        String title = item.getDisplayTitle();

        viewHolder.mTitle.setText(title);

        // item이 ThemeItem 타입인 경우 배경색 설정
        if (item instanceof ThemeItem) {
            int color = ((ThemeItem) item).getColor();
            viewHolder.mTitle.setBackgroundColor(ContextCompat.getColor(mContext, color));

            // 맨 위와 맨 아래 뷰의 모서리를 둥글게
            if (position == 0) {
                viewHolder.mTitle.setBackgroundResource(R.drawable.theme_back_top);
            } else if (position == mItems.size() - 1) {
                viewHolder.mTitle.setBackgroundResource(R.drawable.theme_back_btm);
            } else {
                Log.d(TAG, "onBindViewHolder: no.....");
            }

            // 선택된 아이템인 경우 상태 변경
            if (position == mSelectedItem) {
                viewHolder.mBar.setVisibility(View.GONE);
                viewHolder.mCheck.setVisibility(View.VISIBLE);
            } else {
                viewHolder.mBar.setVisibility(View.VISIBLE);
                viewHolder.mCheck.setVisibility(View.GONE);
            }
        } else {
            viewHolder.mBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<T> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    public interface OnThemeClickListener {
        void onThemeClick(int position);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSelectedItem(int position) {
        mSelectedItem = position;
        // SharedPreferences에 선택된 아이템 위치 저장
        mPrefs.edit().putInt(PREF_SELECTED_ITEM, position).apply();
        notifyDataSetChanged();
    }

    // 선택된 아이템의 위치를 가져오는 메서드
    public int getSelectedItem() {
        return mSelectedItem;
    }

    private class ThemeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        private final ImageView mBar;
        private final ImageView mCheck;

        public ThemeViewHolder(View view) {
            super(view);
            mTitle = view.findViewById(R.id.btn_color);
            mBar = view.findViewById(R.id.status_bar);
            mCheck = view.findViewById(R.id.check);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    setSelectedItem(position); // 선택된 아이템 설정
                    if (mListener != null) {
                        mListener.onThemeClick(position);
                    }
                }
            });
        }
    }

    class ThemeListSizeLookup extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return 1;
        }

        @Override
        public int getSpanIndex(int position, int spanCount) {
            return mColumnCount;
        }
    }
}
