package com.amay077.stopwatchapp.di;

import com.amay077.stopwatchapp.di.scope.ActivityScope;
import com.amay077.stopwatchapp.views.activities.MainActivity;

import dagger.Subcomponent;

@ActivityScope
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(MainActivity activity);
}
