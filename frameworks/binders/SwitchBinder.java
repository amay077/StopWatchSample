package com.amay077.stopwatchapp.frameworks.binders;

import android.widget.Switch;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * ViewModel と Switch のバインドを行う
 */
public class SwitchBinder extends ButtonBinder {
    private final Switch _switchView;

    public SwitchBinder(Switch switchView) {
        super(switchView);
        _switchView = switchView;
    }

    public SwitchBinder toChecked(Observable<Boolean> prop) {
        _subscriptions.add(
                prop.observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<Boolean>() {
                            @Override
                            public void call(Boolean x) {
                                _switchView.setChecked(x);
                            }
                        }));;
        return this;
    }
}
