package com.amay077.stopwatchapp.frameworks.binders;

import android.view.View;
import android.widget.TextView;

import com.amay077.stopwatchapp.frameworks.Command;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hrnv on 2015/12/20.
 */
public class TextViewBinder extends BaseBinder {
    private final TextView _textView;

    public TextViewBinder(TextView textView) {
        _textView = textView;
    }

    public TextViewBinder toTextOneWay(Observable<String> prop) {
        _subscriptions.add(
            prop.observeOn(AndroidSchedulers.mainThread())
            .subscribe(new Action1<String>() {
                @Override
                public void call(String x) {
                    _textView.setText(x);
                }
            }));

        return this;
    }
}
