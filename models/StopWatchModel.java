package com.amay077.stopwatchapp.models;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.SerializedSubject;

/**
 * ストップウォッチの機能を実装したロジッククラス
 */
public class StopWatchModel implements Subscription {
    // ストップウォッチの状態を更新＆通知するための Subject 群
    private final BehaviorSubject<Long> _time = BehaviorSubject.<Long>create(0L); // タイマー時間
    private final BehaviorSubject<Boolean> _isRunning = BehaviorSubject.<Boolean>create(false); // 実行中か？
    private final BehaviorSubject<List<Long>> _laps = BehaviorSubject.<List<Long>>create(Collections.<Long>emptyList()); // 経過時間群
    private final BehaviorSubject<Boolean> _isVisibleMillis = BehaviorSubject.<Boolean>create(true); // ミリ秒表示するか？

    // スレッドを超えて値を更新(onNext)するための SerializedSubject 群
    private final SerializedSubject<Long, Long> _timeSerialized = new SerializedSubject<Long, Long>(_time);
    private final SerializedSubject<Boolean, Boolean> _isRunningSerialized = new SerializedSubject<Boolean, Boolean>(_isRunning);

    // タイマーの購読状況
    private Subscription _timerSubscription = null;

    // Model として公開するプロパティ
    // Subject をそのまま公開したくないので Observable<> にしている
    public final Observable<Long> time = _time;
    public final Observable<Boolean> isRunning = _isRunning;
    public final Observable<List<Long>> laps = _laps;
    public final Observable<Boolean> isVisibleMillis = _isVisibleMillis;

    /**
     * 計測を開始または終了する(time プロパティの更新を開始する)
     */
    public void startOrStop() {
        if (!_isRunning.getValue()) {
            start();
        } else {
            stop();
        }
    }

    /**
     * 経過時間を記録する(経過時間を laps プロパティに追加する)
     */
    public void lap() {
        final List<Long> newLaps = new ArrayList<>();
        newLaps.addAll(_laps.getValue());
        newLaps.add(_time.getValue());

        _laps.onNext(Collections.unmodifiableList(newLaps));
    }

    /**
     * 小数点以下の表示有無を切り替える（isVisibleMillis プロパティを toggle する）
     */
    public void toggleVisibleMillis() {
        _isVisibleMillis.onNext(!_isVisibleMillis.getValue());
    }


    private void start() {
        if (_isRunning.getValue()) {
            stop();
        }

        _timerSubscription =
                Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.newThread())
                .compose(new Observable.Transformer<Long, Long>() {
                    @Override
                    public Observable<Long> call(Observable<Long> x) {
                        // 開始時に Laps をクリア、実行中フラグをON
                        _laps.onNext(Collections.<Long>emptyList());
                        _isRunningSerialized.onNext(true);
                        return x;
                    }
                })
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long time) {
                        // タイマー値を通知
                        _timeSerialized.onNext(time);
                    }
                });
    }

    private void stop() {
        if (_timerSubscription != null) {
            _timerSubscription.unsubscribe();
            _timerSubscription = null;
        }

        // 実行終了を通知
        _isRunningSerialized.onNext(false);
    }

    // Subscription インターフェースの実装
    @Override
    public void unsubscribe() {
        stop();
    }

    @Override
    public boolean isUnsubscribed() {
        return _timerSubscription == null;
    }
}
