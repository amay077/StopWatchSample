package com.amay077.stopwatchapp.frameworks.binders;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hrnv on 2015/12/20.
 */
public abstract class BaseBinder implements Subscription {
    protected final CompositeSubscription _subscriptions = new CompositeSubscription();

    @Override
    public void unsubscribe() {
        _subscriptions.unsubscribe();
        _subscriptions.clear();
    }

    @Override
    public boolean isUnsubscribed() {
        return _subscriptions.isUnsubscribed();
    }
}
