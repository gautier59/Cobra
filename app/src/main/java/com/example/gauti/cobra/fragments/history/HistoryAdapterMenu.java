package com.example.gauti.cobra.fragments.history;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.model.Alerte;
import com.example.gauti.cobra.provider.AlerteProvider;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by mobilefactory on 13/02/2017.
 */

public class HistoryAdapterMenu extends ArrayAdapter<Alerte> {
    private Context mContext;
    private List<Alerte> mAlerteList;
    private int mIdAlerte;

    public HistoryAdapterMenu(Context context, List<Alerte> alertes) {
        super(context, 0, alertes);
        mContext = context;
        mAlerteList = alertes;
    }

    @NonNull
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_alerte_menu, parent, false);
        }

        HistoryAdapterMenu.HistoryViewHolder viewHolder = (HistoryAdapterMenu.HistoryViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new HistoryAdapterMenu.HistoryViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDateRow);
            viewHolder.delete = (Button) convertView.findViewById(R.id.btn_delete);
            convertView.setTag(viewHolder);
        }

        final Alerte alerte = getItem(position);

        viewHolder.date.setText(alerte.getDate());

        viewHolder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(mContext)
                        .setTitle(mContext.getResources().getString(R.string.popover_history_delete_title))
                        .setMessage(mContext.getResources().getString(R.string.popover_history_delete_text))
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mIdAlerte = mAlerteList.get(position).getId();
                                AlerteProvider.deleteAlerte(mContext, mIdAlerte);
                                mAlerteList.remove(position);
                                notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .create().show();
            }
        });

        return convertView;
    }

    @Nullable
    @Override
    public Alerte getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private class HistoryViewHolder {
        public TextView date;
        public Button delete;
    }
}
