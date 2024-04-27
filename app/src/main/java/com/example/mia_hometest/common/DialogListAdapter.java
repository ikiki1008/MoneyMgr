package com.example.mia_hometest.common;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mia_hometest.R;

import java.util.ArrayList;
import java.util.List;

public class DialogListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private final Context mContext;
    private final int mColumnCount;
    private final LayoutInflater mInflater;
    private static final int TILE_VIEW_TYPE = 1;
    private final DialogSizeLookUp mDialogLookup = new DialogSizeLookUp();
    private List<DialogItem> mTiles = new ArrayList<>();
    private OnItemClickListener  mListener;

    public DialogListAdapter (Context context, OnItemClickListener listener) {
        mContext = context;
        mColumnCount = 1;
        mListener = listener;
        mInflater = LayoutInflater.from(context);
    }

    public interface OnItemClickListener  {
        void onItemClick (int position);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DialogViewHolder(mInflater.inflate(
                R.layout.dialog_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        DialogItem tile = mTiles.get(position);
        DialogViewHolder dialogViewHolder = (DialogViewHolder) holder;

        String title = tile.getTitle();
        String note = tile.getDesc();

        dialogViewHolder.mText.setText(title);
        dialogViewHolder.mNote.setText(note);
        holder.itemView.setVisibility(tile.isVisible() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() { return mTiles.size(); }

    @SuppressLint("NotifyDataSetChanged")
    public void setItems(List<DialogItem> items) {
        for (int i = mTiles.size() - 1; i >= 0; i--) {
            notifyItemRemoved(i);
        }
        mTiles.clear();

        // 새로운 아이템들 추가
        for (int i = 0; i < items.size(); i++) {
            mTiles.add(items.get(i));
            notifyItemInserted(i);
        }
//        mTiles = items;
        notifyDataSetChanged();
    }

    private class DialogViewHolder extends RecyclerView.ViewHolder {
        private final TextView mText;
        private final TextView mNote;

        public DialogViewHolder(View itemView) {
            super(itemView);
            mText = itemView.findViewById(R.id.text);
            mNote = itemView.findViewById(R.id.notes);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mListener != null) {
                        mListener.onItemClick(getAdapterPosition());
                    }
                }
            });
        }
    }

    class DialogSizeLookUp extends GridLayoutManager.SpanSizeLookup {
        @Override
        public int getSpanSize(int position) {
            return 1;
        }

        @Override
        public int getSpanIndex (int position, int spanCount) { return mColumnCount; }
    }
}
