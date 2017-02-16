package com.example.gauti.cobra.fragments.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.model.Alerte;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mobilefactory on 13/02/2017.
 */

public class HistoryAdapterMenu extends RecyclerView.Adapter<HistoryAdapterMenu.HistoryViewHolder> {

    // Private fields
    // --------------------------------------------------------------------------------------------
    private Context mContext;
    private List<Alerte> mAlerteList;

    // Constructor
    // --------------------------------------------------------------------------------------------
    public HistoryAdapterMenu(Context mContext, List<Alerte> mAlerteList) {
        this.mContext = mContext;
        this.mAlerteList = mAlerteList;
    }

    // Adapter implementation
    // --------------------------------------------------------------------------------------------
    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_alerte, parent, false);
        return new HistoryViewHolder(view, new HistoryViewHolder.HistoryViewHolderClickListener() {
            @Override
            public void onItemClick(int position) {
                if (!mAlerteList.get(position).getDate().isEmpty()) {
                    //TODO ajouter le marker
                }
            }
        });
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        Alerte alerte = mAlerteList.get(position);

        holder.mTvDateRow.setText(alerte.getDate());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    // View holder
    // --------------------------------------------------------------------------------------------
    public static class HistoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @Bind(R.id.tvDateRow)
        TextView mTvDateRow;

        HistoryViewHolderClickListener mListener;

        HistoryViewHolder(View itemView, HistoryViewHolderClickListener listener) {
            super(itemView);

            ButterKnife.bind(this, itemView);

            mListener = listener;

            mTvDateRow.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onItemClick(getAdapterPosition());
        }

        public interface HistoryViewHolderClickListener {
            void onItemClick(int position);
        }
    }
}
