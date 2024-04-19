package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;

import java.util.ArrayList;
import java.util.List;

public class TestGridAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final Context mContext;
    private final int mColumnCount;
    private final LayoutInflater mInflater;
    private static final int TILE_VIEW_TYPE = 1;
    private final GridSpanSizeLookup mSpanSizeLookup = new GridSpanSizeLookup();
    private List<ListItem> mTiles = new ArrayList<>();

    public TestGridAdapter (Context context) {
        mContext = context;
        mColumnCount = 1;
        mInflater = LayoutInflater.from(context);
    }

    public GridLayoutManager getGridManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,1);
        gridLayoutManager.setSpanSizeLookup(mSpanSizeLookup);
        return gridLayoutManager;
    }

    public interface Tile {
        @Nullable
        Drawable getIcon();
        String getTitle();
        String getDesc();
        String getPrice();
        String getDate();
//        void setEnable(boolean enable);
//        View.OnClickListener onClickListener();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(mInflater.inflate(
                R.layout.list_tile, parent, false));


//        switch (viewType) {
//            case TILE_VIEW_TYPE:
//                return new ListViewHolder(mInflater.inflate(
//                        R.layout.list_tile, parent, false));
//            default:
//                throw new RuntimeException("unknown view type ... : " + viewType);
//        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListItem tile = mTiles.get(position);
        ListViewHolder listViewHolder= (ListViewHolder) holder;

//        listViewHolder.mIcon.setImageResource(tile.getIcon());
        String title = tile.getTitle();
        String desc = tile.getDesc();
        String price = tile.getPrice();
        String date = tile.getDate();

        listViewHolder.mTitle.setText(title);
        listViewHolder.mDesc.setText(desc);
        listViewHolder.mPrice.setText(price);
        listViewHolder.mDate.setText(date);

//        if (!TextUtils.isEmpty(title)) {
//            listViewHolder.mTitle.setText(title);
//        } else if (!TextUtils.isEmpty(desc)) {
//            listViewHolder.mDesc.setText(desc);
//        } else if (!TextUtils.isEmpty(price)) {
//            listViewHolder.mPrice.setText(price);
//        } else if (!TextUtils.isEmpty(date)) {
//            listViewHolder.mDate.setText(date);
//        }
    }

    private void setTextNotEmpty(TextView textView, String text) {
        if (!TextUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    @Override
    public int getItemCount() {
        return mTiles.size();
    }

    public List<ListItem> getItems() {
        return mTiles;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<ListItem> items) {
        mTiles = items;
        notifyDataSetChanged();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
//        private final ImageView mIcon;
        private final TextView mTitle;
        private final TextView mDesc;
        private final TextView mPrice;
        private final TextView mDate;

        ListViewHolder(View itemView) {
            super(itemView);
//            mIcon = itemView.findViewById(R.id.icon);
            mTitle = itemView.findViewById(R.id.title);
            mDesc = itemView.findViewById(R.id.desc);
            mPrice = itemView.findViewById(R.id.price);
            mDate = itemView.findViewById(R.id.date);
        }
    }

    class GridSpanSizeLookup extends GridLayoutManager.SpanSizeLookup {
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
