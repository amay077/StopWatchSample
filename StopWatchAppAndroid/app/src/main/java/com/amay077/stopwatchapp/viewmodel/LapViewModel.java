package com.amay077.stopwatchapp.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;

import com.amay077.databinding.RxField;
import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.models.StopWatchModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by hrnv on 2015/12/20.
 */
public class LapViewModel implements Subscription {
    private final Context _appContext;
    private final StopWatchModel _stopWatch;

    private StopWatchModel getStopWatch() {
        return ((App)_appContext).getStopWatch();
    }


    /** 経過時間群 */
    public final ObservableField<List<LapItem>> formattedLaps;

    private final CompositeSubscription _subscriptions = new CompositeSubscription();

    public LapViewModel(Context appContext) {
        _appContext = appContext;
        _stopWatch = getStopWatch();

        formattedLaps = new RxField<>(_stopWatch.formatTimesAsObservable(_stopWatch.laps)
                .map(new Func1<List<String>, List<LapItem>>() {
                    @Override
                    public List<LapItem> call(List<String> fLaps) {
                        final List<LapItem> lapItems = new ArrayList<>();
                        int i = 1;
                        for (String lap : fLaps) {
                            lapItems.add(new LapItem(String.valueOf(i), lap));
                            i++;
                        }

                        return Collections.unmodifiableList(lapItems);
                    }
                }));
    }

    @Override
    public void unsubscribe() {
        _subscriptions.unsubscribe();
    }

    @Override
    public boolean isUnsubscribed() {
        return _subscriptions.isUnsubscribed();
    }
}
