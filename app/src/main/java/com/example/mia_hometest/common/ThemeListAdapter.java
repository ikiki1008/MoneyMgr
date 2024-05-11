package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;

import java.util.ArrayList;
import java.util.List;

public class ThemeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = ThemeListAdapter.class.getSimpleName();
    private final Context mContext;
    private final int mColumnCount;
    private final LayoutInflater mInflater;
    private static final int TILE_VIEW_TYPE = 1;
    private final ThemeListSizeLookup mThemeLookup = new ThemeListSizeLookup();
    private List<ThemeItem> mTile = new ArrayList<>();
    private OnThemeClickListener mListener;
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ThemeViewHolder(mInflater.inflate(
                R.layout.theme_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ThemeItem item = mTile.get(position);
        ThemeViewHolder viewHolder = (ThemeViewHolder) holder;
        String title = item.getTitle();
        int color = item.getColor();
        viewHolder.mTitle.setText(title);
        viewHolder.mTitle.setBackgroundColor(ContextCompat.getColor(mContext, color));

        //맨 위와 맨 아래 뷰의 모서리를 둥글게
        if (position == 0) {
            viewHolder.mTitle.setBackgroundResource(R.drawable.theme_back_top);
        } else if (position == mTile.size() - 1 ) {
            viewHolder.mTitle.setBackgroundResource(R.drawable.theme_back_btm);
        } else {
            Log.d(TAG, "onBindViewHolder: no.....");
        }
    }

    @Override
    public int getItemCount() {
        return mTile.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ThemeItem> items) {
        mTile = items;
        notifyDataSetChanged();
    }

    public ThemeListAdapter (Context context, OnThemeClickListener listener) {
        mContext = context;
        mColumnCount = 1;
        mListener = listener;
        mInflater = LayoutInflater.from(context);
    }

    public interface OnThemeClickListener {
        void onThemeClick (int position);
    }

    private class ThemeViewHolder extends RecyclerView.ViewHolder {
        private final TextView mTitle;
        public ThemeViewHolder (View view) {
            super(view);
            mTitle = view.findViewById(R.id.btn_color);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onThemeClick(getAdapterPosition());
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
        public int getSpanIndex (int position, int spanCount) { return mColumnCount; }
    }
}
