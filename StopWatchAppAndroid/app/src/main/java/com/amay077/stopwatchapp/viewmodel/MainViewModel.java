package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.frameworks.Command;
import com.amay077.stopwatchapp.frameworks.messengers.Messenger;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.models.StopWatchModel;
import com.amay077.stopwatchapp.views.LapActivity;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;

public class MainViewModel implements Subscription {

    // 簡易Messenger(EventBus のようなもの)
    public final Messenger messenger = new Messenger();

    private final StopWatchModel _stopWatch;

    // ■ViewModel として公開するプロパティ

    /** タイマー時間 */
    public final Observable<Long> time;
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
        time = _stopWatch.time.throttleFirst(10, TimeUnit.MILLISECONDS);
        // ミリ秒以下表示有無に応じて、format書式文字列を切り替え（これはModelでやるべき？）
        timeFormat = _stopWatch.isVisibleMillis.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isVisibleMillis) {
                return isVisibleMillis ? "mm:ss.SSS" : "mm:ss";
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
