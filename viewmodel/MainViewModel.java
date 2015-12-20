package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.models.StopWatchModel;

public class MainViewModel {

    private final Context _appContext;

    public MainViewModel(Context appContext) {
        _appContext = appContext;
    }

    public StopWatchModel getStopWatch() {
        return ((App)_appContext).getStopWatch();
    }
}
