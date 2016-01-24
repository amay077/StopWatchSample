package com.amay077.stopwatchapp.frameworks.messengers;

import com.amay077.stopwatchapp.views.activities.LapActivity;

public class StartActivityMessage implements Message {
    public final Class<LapActivity> activityClass;

    public StartActivityMessage(Class<LapActivity> activityClass) {
        this.activityClass = activityClass;
    }
}
