package com.amay077.stopwatchapp.views.adapters;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.databinding.ObservableField;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ListItemLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapItem;

import java.util.List;

import rx.Subscription;

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

//    public Subscription setLaps(final ObservableField<List<LapItem>> laps) {
//        final Observable.OnPropertyChangedCallback onPropertyChanged = new Observable.OnPropertyChangedCallback() {
//            @Override
//            public void onPropertyChanged(Observable sender, int propertyId) {
//                clear();
//                addAll(laps.get());
//            }
//        };
//
//        laps.addOnPropertyChangedCallback(onPropertyChanged);
//
//        return new Subscription() {
//            @Override
//            public void unsubscribe() {
//                laps.removeOnPropertyChangedCallback(onPropertyChanged);
//            }
//
//            @Override
//            public boolean isUnsubscribed() {
//                return true;
//            }
//        };
//    }
}
