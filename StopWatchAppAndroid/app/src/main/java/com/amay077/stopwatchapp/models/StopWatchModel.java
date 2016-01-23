package com.amay077.stopwatchapp.models;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
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

    public StopWatchModel() {

    }

    /**
     * フォーマットされた時間を表す Observable（timeObservable と isVisibleMillis のどちらかが変更されたら更新）
     */
    public Observable<String> formatTimeAsObservable(Observable<Long> timeObservable) {
        return Observable.combineLatest(
                timeObservable,
                isVisibleMillis, new Func2<Long, Boolean, String>() {
                    @Override
                    public String call(Long time, Boolean isVisibleMillis) {
                        final String format = getTimeFormat(isVisibleMillis);
                        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                        return sdf.format(new Date(time));
                    }
                });
    }

    /**
     * フォーマットされた経過時間群を表す Observable（timesObservable と isVisibleMillis のどちらかが変更されたら更新）
     */
    public Observable<List<String>> formatTimesAsObservable(Observable<List<Long>> timesObservable) {
        return Observable.combineLatest(
                timesObservable,
                isVisibleMillis, new Func2<List<Long>, Boolean, List<String>>() {
                    @Override
                    public List<String> call(List<Long> laps, Boolean isVisibleMillis) {
                        final String format = getTimeFormat(isVisibleMillis);
                        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

                        final List<String> formattedLaps = new ArrayList<>();
                        for (int i = 0; i < laps.size(); i++) {
                            formattedLaps.add((i+1) + ".  " + sdf.format(new Date(laps.get(i))));
                        }

                        return formattedLaps;
                    }
                });
    }

    private String getTimeFormat(boolean isVisibleMillis) {
        return isVisibleMillis ? "mm:ss.SSS" : "mm:ss";
    }

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

    private void start() {
        if (_isRunning.getValue()) {
            stop();
        }

        final AtomicLong startTime = new AtomicLong();

        _timerSubscription =
                Observable.interval(1, TimeUnit.MILLISECONDS, Schedulers.newThread())
                        .compose(new Observable.Transformer<Long, Long>() {
                            @Override
                            public Observable<Long> call(Observable<Long> x) {
                                // 開始時に Laps をクリア、実行中フラグをON
                                _laps.onNext(Collections.<Long>emptyList());
                                _isRunningSerialized.onNext(true);
                                startTime.set(System.currentTimeMillis());
                                return x;
                            }
                        })
                        .subscribe(new Action1<Long>() {
                            @Override
                            public void call(Long notUse) {
                                // タイマー値を通知
                                _timeSerialized.onNext(System.currentTimeMillis() - startTime.get());
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

    /**
     * 経過時間を記録する(経過時間を laps プロパティに追加する)
     */
    public void lap() {
        final List<Long> laps = _laps.getValue();
        final List<Long> newLaps = new ArrayList<>();

        newLaps.addAll(laps);

        long totalLap = 0L;
        for (Long lap:laps) {
            totalLap += lap;
        }

        newLaps.add(_time.getValue() - totalLap);

        _laps.onNext(Collections.unmodifiableList(newLaps));
    }

    /** 最速ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい) */
    public Long getFastestLap() {
        final List<Long> laps = _laps.getValue();
        if (laps.size() == 0) {
            return null; // nullを使うことを許したまえ
        } else {
            return Collections.min(laps);
        }
    }

    /** 最遅ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい) */
    public Long getWorstLap() {
        final List<Long> laps = _laps.getValue();
        if (laps.size() == 0) {
            return null; // nullを使うことを許したまえ
        } else {
            return Collections.max(laps);
        }
    }

    /**
     * 小数点以下の表示有無を切り替える（isVisibleMillis プロパティを toggle する）
     */
    public void toggleVisibleMillis() {
        _isVisibleMillis.onNext(!_isVisibleMillis.getValue());
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
