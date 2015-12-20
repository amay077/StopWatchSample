package com.amay077.stopwatchapp.frameworks.binders;

import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.amay077.stopwatchapp.frameworks.Command;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by hrnv on 2015/12/20.
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
