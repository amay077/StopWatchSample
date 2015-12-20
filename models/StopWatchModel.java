package com.amay077.stopwatchapp.models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rx.Observable;

public class StopWatchModel {

    public final Observable<Long> time = Observable.just(0L);
    public final Observable<List<Long>> laps = Observable.<List<Long>>just(new ArrayList<Long>());

    public StopWatchModel() {
    }

    public void start() {

    }

    public void stop() {

    }

    public void lap() {

    }

}
