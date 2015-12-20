package com.amay077.stopwatchapp.frameworks.binders;

import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class ArrayAdapterBinder extends BaseBinder  {
    private final ArrayAdapter<String> _adapter;

    public ArrayAdapterBinder(ArrayAdapter<String> adapter) {
        _adapter = adapter;
    }

    public ArrayAdapterBinder toItems(Observable<List<String>> prop) {
        _subscriptions.add(prop
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> items) {
                        _adapter.clear();
                        _adapter.addAll(items);
                    }
                }));
        return this;
    }
}
