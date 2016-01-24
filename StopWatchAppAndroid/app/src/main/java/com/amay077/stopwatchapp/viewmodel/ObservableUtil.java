package com.amay077.stopwatchapp.viewmodel;

import android.databinding.ObservableField;

import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hrnv on 16/01/24.
 */
public final class ObservableUtil {
    private ObservableUtil() {
    }

    /**
     * rx.Observable から ObservableField への変換をおこなう
     */
    static public <T> ObservableField<T> toObservableField(Observable<T> source, CompositeSubscription subscriptions) {
        final ObservableField<T> field = new ObservableField<T>();

        subscriptions.add(
                // TODO onError も拾ったほうがいい
                source.subscribe(new Action1<T>() {
                    @Override
                    public void call(T x) {
                        field.set(x);
                    }
                })
        );

        return field;
    }
}
