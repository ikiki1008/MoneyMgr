package com.example.mia_hometest.common;

import static androidx.core.content.ContextCompat.startActivity;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;
import com.example.mia_hometest.UserMainActivity;
import com.example.mia_hometest.fragments.main.CardScreenFragment;

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
    private boolean mCheckItemDelete;

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

    public void setSwipeListener (OnSwipeListener listener, boolean delete) {
        mSwipe = listener;
        mCheckItemDelete = delete;
    }

    public interface OnSwipeListener {
        void onSwipeLeft (int position, boolean delete);
        void onSwipeRight (int position);
    }

    public interface Tile {
        @Nullable
        Drawable getIcon();
        String getTitle();
        String getDesc();
        String getPrice();
        String getDate();
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

        listViewHolder.mTitle.setText(title);
        listViewHolder.mPrice.setText(price);
        listViewHolder.mDate.setText(date);

        View view = listViewHolder.itemView;
        ImageView swipeImage = listViewHolder.mDelete;

        listViewHolder.itemView.setOnTouchListener(new View.OnTouchListener() {
            private float startX;
            private static final int MIN_DISTANCE = 150;
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch(event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        break;
                    case MotionEvent.ACTION_UP:
                        float endX = event.getX();
                        float deltaX = endX - startX;

                        // 왼쪽으로 스와이프했을 때 처리
                        if (deltaX < 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            // 오른쪽으로 75dp 이동 애니메이션 추가
                            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", 0, -convertDpToPixel(60, mContext));
                            animator.setDuration(300);
                            animator.start();
                            swipeImage.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    Log.d(TAG, "onClick: 어댑터를 클릭했다 ");
                                    if (mSwipe != null) {
                                        mSwipe.onSwipeLeft(position, true);
                                    }
                                }
                            });
                        } else if (deltaX > 0 && Math.abs(deltaX) > MIN_DISTANCE) {
                            // 오른쪽으로 스와이프하여 원래 위치로 돌아오는 애니메이션 추가
                            ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", v.getTranslationX(), 0);
                            animator.setDuration(300);
                            animator.start();
                        }
                        break;
                }
                return true; // true 반환하여 이벤트를 소비하도록 설정
            }
        });
    }

    private float convertDpToPixel(float dp, Context context) {
        return dp * context.getResources().getDisplayMetrics().density;
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

    public boolean isItemDelete (boolean delete, int position) {
        if (delete) {
            return true;
        }
        return false;
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
        private final TextView mTitle;
        private final TextView mPrice;
        private final TextView mDate;
        private final ImageView mDelete;

        ListViewHolder(View itemView) {
            super(itemView);
//            mIcon = itemView.findViewById(R.id.icon);
            mTitle = itemView.findViewById(R.id.title);
            mPrice = itemView.findViewById(R.id.price);
            mDate = itemView.findViewById(R.id.date);
            mDelete = itemView.findViewById(R.id.delete);
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
