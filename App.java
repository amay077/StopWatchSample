package com.amay077.stopwatchapp;

import android.app.Application;

import com.amay077.stopwatchapp.models.StopWatchModel;

public class App extends Application {

    private final StopWatchModel _stopWatch = new StopWatchModel();

    public StopWatchModel getStopWatch() {
        return _stopWatch;
    }

}
