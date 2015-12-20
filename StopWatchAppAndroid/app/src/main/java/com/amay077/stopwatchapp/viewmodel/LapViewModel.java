package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.models.StopWatchModel;

import java.util.List;

import rx.Observable;
import rx.functions.Func1;

/**
 * Created by hrnv on 2015/12/20.
 */
public class LapViewModel {
    private final Context _appContext;
    private final StopWatchModel _stopWatch;

    private StopWatchModel getStopWatch() {
        return ((App)_appContext).getStopWatch();
    }

    /** 経過時間群 */
    public final Observable<List<Long>> laps;
    /** 時間の表示フォーマット */
    public final Observable<String> timeFormat;

    public LapViewModel(Context appContext) {
        _appContext = appContext;
        _stopWatch = getStopWatch();

        laps = _stopWatch.laps;
        // ミリ秒以下表示有無に応じて、format書式文字列を切り替え（これはModelでやるべき？）
        timeFormat = _stopWatch.isVisibleMillis.map(new Func1<Boolean, String>() {
            @Override
            public String call(Boolean isVisibleMillis) {
                return isVisibleMillis ? "mm:ss.SSS" : "mm:ss";
            }
        });
    }
}
