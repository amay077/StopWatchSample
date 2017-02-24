package com.amay077.stopwatchapp.views.binders;

import android.databinding.BindingAdapter;
import android.databinding.Observable;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import io.reactivex.functions.Cancellable;
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty;

/**
 * Created by h_okuyama on 2017/02/24.
 */

public class ListBinders {
    /**
     * ListView と ViewModel のカスタムバインディング
     *
     * TODO 本当は viewModel.formattedLaps とバインドしたい
     */
    @BindingAdapter("stringListExProp")
    public static void setFormattedLaps(ListView listView, final ReadOnlyRxProperty<List<String>> listProperty) {
        final ArrayAdapter<String> adapter = new ArrayAdapter(
                listView.getContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        adapter.addAll(listProperty.get());

        final Observable.OnPropertyChangedCallback callback = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
                adapter.clear();
                adapter.addAll(listProperty.get());
            }
        };

        listProperty.addOnPropertyChangedCallback(callback);
        listProperty.setCancellable(new Cancellable() {
            @Override
            public void cancel() throws Exception {
                listProperty.removeOnPropertyChangedCallback(callback);
            }
        });
    }
}
