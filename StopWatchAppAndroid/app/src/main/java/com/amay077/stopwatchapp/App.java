package com.amay077.stopwatchapp;

import android.app.Application;

import com.amay077.stopwatchapp.di.AppComponent;
import com.amay077.stopwatchapp.di.AppModule;
import com.amay077.stopwatchapp.di.DaggerAppComponent;
import com.amay077.stopwatchapp.models.StopWatchModel;

public class App extends Application {

    private AppComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        initializeInjector();
    }

    private void initializeInjector() {
        applicationComponent = DaggerAppComponent.builder()
                .appModule(new AppModule(this))
                .build();
    }

    public AppComponent getApplicationComponent() {
        return applicationComponent;
    }

    private final StopWatchModel _stopWatch = new StopWatchModel();

    public StopWatchModel getStopWatch() {
        return _stopWatch;
    }

    @Override
    public void onTerminate() {
        _stopWatch.unsubscribe();
        super.onTerminate();
    }
}
