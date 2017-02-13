package com.example.gauti.cobra.fragments.history;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.gauti.cobra.R;
import com.example.gauti.cobra.model.Alerte;

import java.util.List;

/**
 * Created by mobilefactory on 13/02/2017.
 */

public class AlerteAdapter extends ArrayAdapter<Alerte> {

    public AlerteAdapter(Context context, List<Alerte> alertes) {
        super(context, 0, alertes);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_alerte, parent, false);
        }

        AlerteViewHolder viewHolder = (AlerteViewHolder) convertView.getTag();
        if (viewHolder == null) {
            viewHolder = new AlerteViewHolder();
            viewHolder.date = (TextView) convertView.findViewById(R.id.tvDateRow);
            convertView.setTag(viewHolder);
        }

        Alerte alerte = getItem(position);

        viewHolder.date.setText(alerte.getDate());

        return convertView;
    }

    private class AlerteViewHolder {
        public TextView date;
    }
}
