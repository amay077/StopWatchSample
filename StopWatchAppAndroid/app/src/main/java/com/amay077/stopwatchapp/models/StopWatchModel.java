package com.amay077.stopwatchapp.models;

import com.annimon.stream.Optional;
import com.annimon.stream.Stream;

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
    private final Subject<Long> _timeSerialized = _time.toSerialized();
    private final Subject<Boolean> _isRunningSerialized = _isRunning.toSerialized();

    // タイマーの購読状況
    private Disposable _timerSubscription = null;

    private String _formattedFastestLap;
    private String _formattedWorstLap;

    // Model として公開するプロパティ
    // Subject をそのまま公開したくないので Observable<> にしている
    public final Observable<String> formattedTime;
    public final Observable<List<String>> formattedLaps;
    public final Observable<Boolean> isRunning = _isRunning;
    public final Observable<Boolean> isVisibleMillis = _isVisibleMillis;

    private final AtomicLong _startTime = new AtomicLong(0L);

    private String formatTime(long t, String format) {
        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());
        return sdf.format(new Date(t));
    }

    public StopWatchModel() {
        Observable<String> _timeFormat = _isVisibleMillis.map(visible -> visible ? "mm:ss.SSS" : "mm:ss");

        formattedTime = Observable.combineLatest(_time, _timeFormat,
                (t, f) -> formatTime(t, f));

        formattedLaps = Observable.combineLatest(_laps, _timeFormat,
                (laps, f) ->  {
                    Stream<Long> stream = Stream.of(laps);

                    // NOTE ここで max/min を得るのは副作用なのでバッドパターンになり得るが、
                    //      今回は用途が限定的なのでご勘弁。
                    Optional<Long> max = stream.max((x, y)-> x.compareTo(y));
                    _formattedWorstLap = formatTime(max.orElse(0L), f);
                    Optional<Long> min = Stream.of(laps).min((x, y)-> x.compareTo(y));
                    _formattedFastestLap = formatTime(min.orElse(0L), f);

                    return Stream.of(laps).map(l -> formatTime(l, f)).toList();
                });
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
                            updateTime();
                        });
    }

    private void updateTime() {
        _timeSerialized.onNext(System.currentTimeMillis() - _startTime.get());
    }

    private void stop() {

        updateTime();

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

        updateTime();
        newLaps.add(_time.getValue() - totalLap);

        _laps.onNext(Collections.unmodifiableList(newLaps));
    }

    public String getFormattedFastestLap() {
        return _formattedFastestLap;
    }

    public String getFormattedWorstLap() {
        return _formattedWorstLap;
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
