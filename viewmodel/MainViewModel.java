package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.frameworks.Command;
import com.amay077.stopwatchapp.models.StopWatchModel;

import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.functions.Func1;

public class MainViewModel {

    private final Context _appContext;
    private final StopWatchModel _stopWatch;

    private StopWatchModel getStopWatch() {
        return ((App)_appContext).getStopWatch();
    }

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
        _appContext = appContext;
        _stopWatch = getStopWatch();

        time = _stopWatch.time.throttleFirst(10, TimeUnit.MILLISECONDS); // 表示用に10ms毎に間引き。View側でやってもよいかも。
        isRunning = _stopWatch.isRunning;
        laps = _stopWatch.laps;
        isVisibleMillis = _stopWatch.isVisibleMillis;

        timeFormat = _stopWatch.isVisibleMillis.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isVisibleMillis) {
                return isVisibleMillis ? "mm:ss.SSS" : "mm:ss";
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

}
