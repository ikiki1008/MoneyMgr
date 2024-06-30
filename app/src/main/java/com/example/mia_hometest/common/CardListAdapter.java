package com.example.mia_hometest.common;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class CardListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final String TAG = CardListAdapter.class.getSimpleName();
    private final Context mContext;
    private final int mColumnCount;
    private final LayoutInflater mInflater;
    private static final int TILE_VIEW_TYPE = 1;
    private final GridSpanSizeLookup mSpanSizeLookup = new GridSpanSizeLookup();
    private final String NEW_INTENT = "com.android.intent.NEW_INTENT";
    private List<ListItem> mTiles = new ArrayList<>();
    private OnSwipeListener mSwipe;
    private OnListClickListener mListClick;

    public CardListAdapter(Context context) {
        mContext = context;
        mColumnCount = 1;
        mInflater = LayoutInflater.from(context);
    }

    public GridLayoutManager getGridManager() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(mContext,1);
        gridLayoutManager.setSpanSizeLookup(mSpanSizeLookup);
        return gridLayoutManager;
    }

    public void setSwipeListener (OnSwipeListener listener) {
        mSwipe = listener;
    }

    public void setListClickListener (OnListClickListener listener) { mListClick = listener; }

    public interface OnSwipeListener {
        void onSwipeLeft (int position);
    }

    public interface OnListClickListener {
        void onListClick (int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ListViewHolder(mInflater.inflate(
                R.layout.list_tile, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ListItem tile = mTiles.get(position);
        ListViewHolder listViewHolder= (ListViewHolder) holder;

        String title = tile.getTitle();
        String price = tile.getPrice();
        String date = tile.getDate();
        Drawable image = tile.getImage();

        listViewHolder.mIcon.setImageDrawable(image);
        listViewHolder.mTitle.setText(title);
        listViewHolder.mPrice.setText(price);
        listViewHolder.mDate.setText(date);

        View view = listViewHolder.itemView;
        ImageView move = listViewHolder.mRemove;
        view.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private static final int MIN_DISTANCE = 150;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;

                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = endX - startX;

                        if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            // 왼쪽으로 스와이프했을 때 처리
                            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", 0, -convertDpToPixel(60, mContext));
                            animator.setDuration(300);
                            animator.start();
                            move.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: 어댑터를 클릭했다 ");
                                    if (mSwipe != null) {
                                        mSwipe.onSwipeLeft(position);
                                    }
                                }
                            });
                        } else if (deltaX > 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            // 오른쪽으로 스와이프하여 원래 위치로 돌아오는 애니메이션 추가
                            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", v.getTranslationX(), 0);
                            animator.setDuration(300);
                            animator.start();
                        } else {
                            //그냥 클릭만 했을 때 처리
                            if (mListClick != null) {
                                mListClick.onListClick(position);
                            }
                        }
                        break;
                }
                return true;
            }
        });
    }

    private float convertDpToPixel(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
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
        private final ImageView mIcon;
        private final TextView mTitle;
        private final TextView mPrice;
        private final TextView mDate;
        private final ImageView mRemove;
        ListViewHolder(View itemView) {
            super(itemView);
            mIcon = itemView.findViewById(R.id.cardlist_icon);
            mTitle = itemView.findViewById(R.id.title);
            mPrice = itemView.findViewById(R.id.price);
            mDate = itemView.findViewById(R.id.date);
            mRemove = itemView.findViewById(R.id.delete_list);
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
