package com.amay077.stopwatchapp.di;

import com.amay077.stopwatchapp.views.activities.LapActivity;
import com.amay077.stopwatchapp.views.activities.MainActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, ActivityModule.class})
public interface AppComponent {
    void inject(MainActivity activity);
    void inject(LapActivity activity);
}
