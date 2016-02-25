package com.amay077.databinding;

import android.databinding.ObservableField;

import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;

public class RxField<T> extends ObservableField<T> {

    private final Observable<T> observable;
    private final Map<Integer, Subscription> sucscriptionMap = new HashMap<Integer, Subscription>();

    public RxField(Observable<T> observable) {
        super();
        this.observable = observable;
    }

    public RxField(Observable<T> observable, T defaultValue) {
        super(defaultValue);
        this.observable = observable;
    }

    @Override
    public synchronized void addOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        super.addOnPropertyChangedCallback(callback);

        sucscriptionMap.put(callback.hashCode(), observable.subscribe(new Action1<T>() {
            @Override
            public void call(T value) {
                set(value);
            }
        }));
    }

    @Override
    public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (sucscriptionMap.containsKey(callback.hashCode())) {
            final Subscription subscription = sucscriptionMap.get(callback.hashCode());
            subscription.unsubscribe();
            sucscriptionMap.remove(callback.hashCode());
        }

        super.removeOnPropertyChangedCallback(callback);
    }

    @Override
    public void set(T value) {
        // TODO should be readonly, because cannot set value to observable
        super.set(value);
    }

    public Observable<T> tObservable() {
        return observable;
    }
}
