package com.amay077.stopwatchapp.models;

import org.reactivestreams.Subscription;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import io.reactivex.Observable;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;

/**
 * ストップウォッチの機能を実装したロジッククラス
 */
public class StopWatchModel {
    // ストップウォッチの状態を更新＆通知するための Subject 群

    private final BehaviorSubject<Long> _time = BehaviorSubject.<Long>createDefault(0L); // タイマー時間
    private final BehaviorSubject<Boolean> _isRunning = BehaviorSubject.<Boolean>createDefault(false); // 実行中か？
    private final BehaviorSubject<List<Long>> _laps = BehaviorSubject.<List<Long>>createDefault(Collections.<Long>emptyList()); // 経過時間群
    private final BehaviorSubject<Boolean> _isVisibleMillis = BehaviorSubject.<Boolean>createDefault(true); // ミリ秒表示するか？

    // スレッドを超えて値を更新(onNext)するための SerializedSubject 群
    private final Subject<Long> _timeSerialized = _time.toSerialized();// new SerializedSubject<Long, Long>(_time);
    private final Subject<Boolean> _isRunningSerialized = _isRunning.toSerialized(); // new SerializedSubject<Boolean, Boolean>(_isRunning);

    // タイマーの購読状況
    private Disposable _timerSubscription = null;

    // Model として公開するプロパティ
    // Subject をそのまま公開したくないので Observable<> にしている
    public final Observable<Long> time = _time;
    public final Observable<Boolean> isRunning = _isRunning;
    public final Observable<List<Long>> laps = _laps;
    public final Observable<Boolean> isVisibleMillis = _isVisibleMillis;

    private final AtomicLong _startTime = new AtomicLong(0L);

    public StopWatchModel() {
    }

    /**
     * フォーマットされた時間を表す Observable（timeObservable と isVisibleMillis のどちらかが変更されたら更新）
     *
     * 拡張メソッドが使えたらなあ。。。
     */
    public Observable<String> formatTimeAsObservable(Observable<Long> timeObservable) {
        return Observable.combineLatest(
                timeObservable,
                isVisibleMillis, (time, isVisibleMillis) -> {
                    final String format = getTimeFormat(isVisibleMillis);
                    final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
                    return sdf.format(new Date(time));
                });
    }

    /**
     * フォーマットされた経過時間群を表す Observable（timesObservable と isVisibleMillis のどちらかが変更されたら更新）
     *
     * 拡張メソッドが使えたらなあ。。。
     */
    public Observable<List<String>> formatTimesAsObservable(Observable<List<Long>> timesObservable) {
        return Observable.combineLatest(
                timesObservable,
                isVisibleMillis, (laps, isVisibleMillis) -> {
                    final String format = getTimeFormat(isVisibleMillis);
                    final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

                    final List<String> formattedLaps = new ArrayList<>();
                    for (Long lap : laps) {
                        formattedLaps.add(sdf.format(new Date(lap)));
                    }

                    return Collections.unmodifiableList(formattedLaps);
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

        _timerSubscription =
                Observable.interval(100, TimeUnit.MILLISECONDS)
                        .compose((Observable<Long> x) -> {
                            // 開始時に Laps をクリア、実行中フラグをON
                            _laps.onNext(Collections.<Long>emptyList());
                            _isRunningSerialized.onNext(true);
                            _startTime.set(System.currentTimeMillis());
                            return x;
                        })
                        .subscribe((Long notUse) -> {
                            // タイマー値を通知
                            _timeSerialized.onNext(System.currentTimeMillis() - _startTime.get());
                        });
    }

    private void stop() {

        _timeSerialized.onNext(System.currentTimeMillis() - _startTime.get());

        if (_timerSubscription != null) {
            _timerSubscription.dispose();
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

        final long lap = System.currentTimeMillis() - _startTime.get();
        newLaps.add(lap - totalLap);

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

    public void unsubscribe() {
        stop();
    }
}
