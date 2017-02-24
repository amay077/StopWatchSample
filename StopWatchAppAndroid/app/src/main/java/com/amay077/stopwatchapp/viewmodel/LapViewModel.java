package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.models.StopWatchModel;

import java.util.List;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import jp.keita.kagurazaka.rxproperty.ReadOnlyRxProperty;

/**
 * Created by hrnv on 2015/12/20.
 */
public class LapViewModel implements Disposable {
    private final Context _appContext;
    private final StopWatchModel _stopWatch;

    private StopWatchModel getStopWatch() {
        return ((App)_appContext).getStopWatch();
    }


    /** 経過時間群 */
    public final ReadOnlyRxProperty<List<String>> formattedLaps;

    private final CompositeDisposable _subscriptions = new CompositeDisposable();

    public LapViewModel(Context appContext) {
        _appContext = appContext;
        _stopWatch = getStopWatch();

        formattedLaps = new ReadOnlyRxProperty<>(_stopWatch.formattedLaps);
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
