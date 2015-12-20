package com.amay077.stopwatchapp.frameworks.binders;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amay077.stopwatchapp.frameworks.Command;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

/**
 * Created by hrnv on 2015/12/20.
 */
public class ButtonBinder extends TextViewBinder {
    private final Button _button;

    public ButtonBinder(Button button) {
        super(button);
        _button = button;
    }

    public TextViewBinder toClickCommand(final Command command) {

        _subscriptions.add(command.canExecuteObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean canExecute) {
                        _button.setEnabled(canExecute);
                    }
                }));

        _button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // command.canExecute をチェックした方がよいが省略
                command.execute();
            }
        });
        return this;
    }
}
