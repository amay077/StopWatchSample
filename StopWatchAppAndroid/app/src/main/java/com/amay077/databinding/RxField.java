package com.amay077.databinding;

import android.databinding.ObservableField;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;

public class RxField<T> extends ObservableField<T> {

    private final Observable<T> observable;
    private final Map<Integer, Disposable> sucscriptionMap = new HashMap<Integer, Disposable>();

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

        sucscriptionMap.put(callback.hashCode(), observable.subscribe(value -> {
            set(value);
        }));
    }

    @Override
    public synchronized void removeOnPropertyChangedCallback(OnPropertyChangedCallback callback) {
        if (sucscriptionMap.containsKey(callback.hashCode())) {
            final Disposable subscription = sucscriptionMap.get(callback.hashCode());
            subscription.dispose();
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
