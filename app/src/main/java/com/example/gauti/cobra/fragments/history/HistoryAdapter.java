package com.example.gauti.cobra.fragments.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.model.Alerte;

import java.util.List;

/**
 * Created by mobilefactory on 13/02/2017.
 */

public class HistoryAdapter extends ArrayAdapter<Alerte> {

    public HistoryAdapter(Context context, List<Alerte> alertes) {
        super(context, 0, alertes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_alerte, parent, false);
        }

        HistoryViewHolder viewHolder = (HistoryViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new HistoryViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDateRow);
            convertView.setTag(viewHolder);
        }

        Alerte alerte = getItem(position);

        viewHolder.date.setText(alerte.getDate());

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
    }
}
