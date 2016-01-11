package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableLong;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.frameworks.Command;
import com.amay077.stopwatchapp.frameworks.messengers.Messenger;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.models.StopWatchModel;
import com.amay077.stopwatchapp.views.LapActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainViewModel implements Subscription {

    // 簡易Messenger(EventBus のようなもの)
    public final Messenger messenger = new Messenger();

    private final StopWatchModel _stopWatch;

    // ■ViewModel として公開するプロパティ

    /** タイマー時間 */
    public final ObservableField<String> formattedTime;
    /** 実行中かどうか？ */
    public final Observable<Boolean> isRunning;
    /** 経過時間群 */
    public final Observable<List<Long>> laps;
    /** 時間の表示フォーマット */
    public final Observable<String> timeFormat;
    /** ミリ秒を表示するか？ */
    public final Observable<Boolean> isVisibleMillis;

    // コンストラクタ
    public MainViewModel(Context appContext) {
        _stopWatch = ((App)appContext).getStopWatch();

        // StopWatchModel のプロパティをそのまま公開してるだけ
        isRunning = _stopWatch.isRunning;
        laps = _stopWatch.laps;
        isVisibleMillis = _stopWatch.isVisibleMillis;

        // 表示用にthrottleで10ms毎に間引き。View側でやってもよいかも。
        formattedTime = new ObservableField<String>("");

        // ミリ秒以下表示有無に応じて、format書式文字列を切り替え（これはModelでやるべき？）
        timeFormat = _stopWatch.isVisibleMillis.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isVisibleMillis) {
                return isVisibleMillis ? "mm:ss.SSS" : "mm:ss";
            }
        });

        // フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        Observable.combineLatest(
                _stopWatch.time.throttleFirst(10, TimeUnit.MILLISECONDS),
                timeFormat, new Func2<Long, String, String>() {
                    @Override
                    public String call(Long time, String format) {
                        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                        return sdf.format(new Date(time));
                    }
                }).subscribe(new Action1<String>() {
            @Override
            public void call(String t) {
                formattedTime.set(t);
            }
        });

        // STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
        isRunning.filter(new Func1<Boolean, Boolean>() {
            @Override
            public Boolean call(Boolean isRunning) {
                return !isRunning;
            }
        })
        .subscribe(new Action1<Boolean>() {
            @Override
            public void call(Boolean notUse) {
                // Toast を表示させる
                messenger.send(new ShowToastMessages(
                        "最速ラップ:" + _stopWatch.getFastestLap() + // FIXME 時間がformatされてない
                        ", 最遅ラップ:" + _stopWatch.getWorstLap()));

                // LapActivity へ遷移させる
                messenger.send(new StartActivityMessage(LapActivity.class)); // ホントは LapViewModel を指定して画面遷移すべき
            }
        });
    }

    // ■ViewModel として公開するコマンド

    /** 開始 or 終了 */
    public final Command commandStartOrStop = new Command() {
        @Override
        public Observable<Boolean> canExecuteObservable() {
            return Observable.just(true); // いつでも実行可能
        }

        @Override
        public void execute() {
            _stopWatch.startOrStop();
        }
    };

    /** 経過時間の記録 */
    public final Command commandLap = new Command() {
        @Override
        public Observable<Boolean> canExecuteObservable() {
            return isRunning; // 実行中のみ記録可能
        }

        @Override
        public void execute() {
            _stopWatch.lap();
        }
    };

    /** ミリ秒以下表示の切り替え */
    public final Command commandToggleVisibleMillis = new Command() {
        @Override
        public Observable<Boolean> canExecuteObservable() {
            return Observable.just(true); // いつでも実行可能
        }

        @Override
        public void execute()  {
            _stopWatch.toggleVisibleMillis();
        }
    };

    @Override
    public void unsubscribe() {
        messenger.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return messenger.isUnsubscribed();
    }
}
