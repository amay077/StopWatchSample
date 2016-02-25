package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.view.View;

import com.amay077.databinding.RxField;
import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.frameworks.messengers.Messenger;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.models.StopWatchModel;
import com.amay077.stopwatchapp.views.activities.LapActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
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
    public final ObservableField<List<LapItem>> formattedLaps;
    /** ミリ秒を表示するか？ */
    public final ObservableField<Boolean> isVisibleMillis;

    // コンストラクタ
    public MainViewModel(Context appContext) {
        _stopWatch = ((App)appContext).getStopWatch();

        // StopWatchModel のプロパティをそのまま公開してるだけ
        isRunning = new RxField<>(_stopWatch.isRunning); // ObservableUtil.toObservableField(_stopWatch.isRunning, _subscriptions);
        formattedLaps = new RxField<>(_stopWatch.formatTimesAsObservable(_stopWatch.laps)
                .map(new Func1<List<String>, List<LapItem>>() {
                    @Override
                    public List<LapItem> call(List<String> fLaps) {
                        final List<LapItem> lapItems = new ArrayList<>();
                        int i = 1;
                        for (String lap : fLaps) {
                            lapItems.add(new LapItem(String.valueOf(i), lap));
                            i++;
                        }

                        return Collections.unmodifiableList(lapItems);
                    }
                }));

        isVisibleMillis = new RxField<>(_stopWatch.isVisibleMillis);

        runButtonTitle = new RxField<>(_stopWatch.isRunning.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isRunning) {
                return isRunning ? "STOP" : "START";
            }
        }));

        // フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        // 表示用にthrottleで10ms毎に間引き。View側でやってもよいかも。
        formattedTime = new RxField<>(_stopWatch.formatTimeAsObservable(_stopWatch.time));

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
        _subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return _subscriptions.isUnsubscribed();
    }
}
