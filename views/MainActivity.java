package com.amay077.stopwatchapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.frameworks.binders.ButtonBinder;
import com.amay077.stopwatchapp.frameworks.binders.SwitchBinder;
import com.amay077.stopwatchapp.frameworks.binders.TextViewBinder;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private /* final */  MainViewModel _viewModel;

    private final CompositeSubscription _subscriptionOnCreate = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _viewModel = new MainViewModel(this.getApplicationContext());

        setContentView(R.layout.activity_main);

        // フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        final Observable<String> formattedTime = Observable.combineLatest(
                _viewModel.time,
                _viewModel.timeFormat, new Func2<Long, String, String>() {
            @Override
            public String call(Long time, String format) {
                final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                return sdf.format(new Date(time));
            }
        });

        // TextView(textTime) のバインド
        _subscriptionOnCreate.add(new TextViewBinder((TextView) findViewById(R.id.textTime))
            .toTextOneWay(formattedTime) // time,timeFormat プロパティを .text へバインド
        );

        // Button(buttonStartStop) のバインド
        _subscriptionOnCreate.add(new ButtonBinder((Button) findViewById(R.id.buttonStartStop))
            .toClickCommand(_viewModel.commandStartOrStop) // コマンド(commandStartOrStop) を Clickイベントへバインド
            .toTextOneWay(_viewModel.isRunning.map(new Func1<Boolean, String>() { // isRunning プロパティを .text へバインド
                @Override
                public String call(Boolean isRunning) {
                    return isRunning ? "STOP" : "START"; // 状態に応じて値(ラベル)を変換
                }
            }))
        );

        // Button(buttonLap) のバインド
        _subscriptionOnCreate.add(new ButtonBinder((Button)findViewById(R.id.buttonLap))
            .toClickCommand(_viewModel.commandLap) // コマンド(commandLap) を Clickイベントへバインド
        );

        // Switch(switchVisibleMillis) のバインド
        _subscriptionOnCreate.add(new SwitchBinder((Switch) findViewById(R.id.switchVisibleMillis))
            .toChecked(_viewModel.isVisibleMillis) // isVisibleMillis プロパティを .checked へバインド
            .toClickCommand(_viewModel.commandToggleVisibleMillis) // コマンド(commandToggleVisibleMillis) を Clickイベントへバインド
        );

    }

    @Override
    protected void onDestroy() {
        // unsubscribe しないと Activity がリークするよ
        _subscriptionOnCreate.unsubscribe();
        _subscriptionOnCreate.clear();
        super.onDestroy();
    }
}
