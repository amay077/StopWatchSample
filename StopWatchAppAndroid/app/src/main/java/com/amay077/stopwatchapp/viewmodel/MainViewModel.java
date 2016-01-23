package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableLong;
import android.view.View;

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
import rx.subscriptions.CompositeSubscription;

public class MainViewModel implements Subscription {

    // 簡易Messenger(EventBus のようなもの)
    public final Messenger messenger = new Messenger();

    private final StopWatchModel _stopWatch;

    private final CompositeSubscription _subscriptions = new CompositeSubscription();

    // ■ViewModel として公開するプロパティ

    /** タイマー時間 */
    public final ObservableField<String> formattedTime;
    /** タイマー時間 */
    public final ObservableField<String> runButtonTitle;
    /** 実行中かどうか？ */
    public final ObservableField<Boolean> isRunning;
    /** 経過時間群 */
    public final Observable<List<String>> formattedLaps;
    /** ミリ秒を表示するか？ */
    public final ObservableField<Boolean> isVisibleMillis;

    // コンストラクタ
    public MainViewModel(Context appContext) {
        _stopWatch = ((App)appContext).getStopWatch();

        // StopWatchModel のプロパティをそのまま公開してるだけ
        isRunning = toObservableField(_stopWatch.isRunning, _subscriptions);
        formattedLaps = _stopWatch.formatTimesAsObservable(_stopWatch.laps);
        isVisibleMillis = toObservableField(_stopWatch.isVisibleMillis, _subscriptions);

        runButtonTitle = toObservableField(_stopWatch.isRunning.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isRunning) {
                return isRunning ? "STOP" : "START";
            }
        }), _subscriptions);

        // フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        // 表示用にthrottleで10ms毎に間引き。View側でやってもよいかも。
        formattedTime = toObservableField(_stopWatch.formatTimeAsObservable(_stopWatch.time), _subscriptions);

        // STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
        _subscriptions.add(
                _stopWatch.isRunning.filter(new Func1<Boolean, Boolean>() {
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
                }));;
    }

    // ■ViewModel として公開するコマンド

    /** 開始 or 終了 */
    public void onClickStartOrStop(View view) {
        _stopWatch.startOrStop();
    }

    /** 経過時間の記録 */
    public void onClickLap(View view) {
        _stopWatch.lap();
    }

    /** ミリ秒以下表示の切り替え */
    public void onClickToggleVisibleMillis(View view) {
        _stopWatch.toggleVisibleMillis();
    }

    @Override
    public void unsubscribe() {
        messenger.unsubscribe();
        _subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return messenger.isUnsubscribed();
    }

    /**
     * rx.Observable から ObservableField への変換をおこなう
     */
    private <T> ObservableField<T> toObservableField(Observable<T> source, CompositeSubscription subscriptions) {
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
