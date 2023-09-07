package com.app.allfilerecovery.view.others.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.app.allfilerecovery.R;

import java.util.List;

public class FilterAdapter extends ArrayAdapter<String> {

    String strUpdateTitle;
    String header;
    public FilterAdapter(@NonNull Context context, int resource, @NonNull List<String> objects, String header) {
        super(context, resource, objects);
        this.header = header;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_filter_selected, parent, false);
        TextView tvFilterSelected = convertView.findViewById(R.id.tv_filter_selected);

        if (strUpdateTitle != null) {
            tvFilterSelected.setText(strUpdateTitle);
        } else {
            tvFilterSelected.setText(header);
        }
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_spinner_filter, parent, false);
        TextView tvFilter = convertView.findViewById(R.id.tv_filter);

        String strFilter = getItem(position);
        if (strFilter != null) {
            tvFilter.setText(strFilter);
        }
        return convertView;
    }

    public void updateTitle(String title) {
        strUpdateTitle = title;
        notifyDataSetChanged();
    }
}
