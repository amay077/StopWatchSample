package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.view.View;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.frameworks.messengers.Messenger;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.models.StopWatchModel;
import com.amay077.stopwatchapp.views.activities.LapActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import jp.keita.kagurazaka.rxproperty.RxProperty;

public class MainViewModel implements Disposable {

    // 簡易Messenger(EventBus のようなもの)
    public final Messenger messenger = new Messenger();

    private final StopWatchModel _stopWatch;

    private final CompositeDisposable _subscriptions = new CompositeDisposable();

    // ■ViewModel として公開するプロパティ

    /** タイマー時間 */
    public final RxProperty<String> formattedTime;
    /** タイマー時間 */
    public final RxProperty<String> runButtonTitle;
    /** 実行中かどうか？ */
    public final RxProperty<Boolean> isRunning;
    /** 経過時間群 */
    public final RxProperty<List<LapItem>> formattedLaps;
    /** ミリ秒を表示するか？ */
    public final RxProperty<Boolean> isVisibleMillis;

    // コンストラクタ
    public MainViewModel(Context appContext) {
        _stopWatch = ((App)appContext).getStopWatch();

        // StopWatchModel のプロパティをそのまま公開してるだけ
        isRunning = new RxProperty<>(_stopWatch.isRunning); // ObservableUtil.toObservableField(_stopWatch.isRunning, _subscriptions);
        formattedLaps = new RxProperty<>(_stopWatch.formatTimesAsObservable(_stopWatch.laps)
                .map(fLaps -> {
                    final List<LapItem> lapItems = new ArrayList<>();
                    int i = 1;
                    for (String lap : fLaps) {
                        lapItems.add(new LapItem(String.valueOf(i), lap));
                        i++;
                    }

                    return Collections.unmodifiableList(lapItems);
                }));

        isVisibleMillis = new RxProperty<>(_stopWatch.isVisibleMillis);

        runButtonTitle = new RxProperty<>(_stopWatch.isRunning.map(isRunning -> {
            return isRunning ? "STOP" : "START";
        }));

        // フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        // 表示用にthrottleで10ms毎に間引き。View側でやってもよいかも。
        formattedTime = new RxProperty<>(_stopWatch.formatTimeAsObservable(_stopWatch.time));

        // STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
        _subscriptions.add(
                _stopWatch.isRunning.filter(isRunning -> {
                    return !isRunning;
                })
                .subscribe(notUse -> {
                    // Toast を表示させる
                    messenger.send(new ShowToastMessages(
                            "最速ラップ:" + _stopWatch.getFastestLap() + // FIXME 時間がformatされてない
                                    ", 最遅ラップ:" + _stopWatch.getWorstLap()));

                    // LapActivity へ遷移させる
                    messenger.send(new StartActivityMessage(LapActivity.class)); // ホントは LapViewModel を指定して画面遷移すべき
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
    public void dispose() {
        _subscriptions.dispose();
    }

    @Override
    public boolean isDisposed() {
        return _subscriptions.isDisposed();
    }
}
