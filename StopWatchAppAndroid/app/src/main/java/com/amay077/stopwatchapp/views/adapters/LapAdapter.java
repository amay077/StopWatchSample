package com.amay077.stopwatchapp.views.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.amay077.stopwatchapp.databinding.ListItemLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapItem;


public final class LapAdapter extends ArrayAdapter<LapItem> {
    private final LayoutInflater inflater;

    public LapAdapter(final Context context) {
        super(context, android.R.layout.simple_list_item_1);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        if (convertView == null) {
            ListItemLapBinding binding = ListItemLapBinding.inflate(inflater, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        }

        LapItem item = getItem(position);
        final ListItemLapBinding binding = (ListItemLapBinding) convertView.getTag();
        binding.setLapItem(item);

        return convertView;
    }
}
