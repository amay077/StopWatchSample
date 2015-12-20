package com.amay077.stopwatchapp.views;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.frameworks.binders.ArrayAdapterBinder;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class LapActivity extends AppCompatActivity {

    private /* final */ LapViewModel _viewModel;

    private final CompositeSubscription _subscriptionOnCreate = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _viewModel = new LapViewModel(this.getApplicationContext());

        setContentView(R.layout.activity_lap);

        // フォーマットされた経過時間群を表す Observable（time と timeFormat のどちらかが変更されたら更新）
        final Observable<List<String>> formattedLaps = Observable.combineLatest(
                _viewModel.laps,
                _viewModel.timeFormat, new Func2<List<Long>, String, List<String>>() {
                    @Override
                    public List<String> call(List<Long> laps, String format) {
                        final SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.getDefault());

                        final List<String> formattedLaps = new ArrayList<>();
                        for (int i = 0; i < laps.size(); i++) {
                            formattedLaps.add((i+1) + ".  " + sdf.format(new Date(laps.get(i))));
                        }

                        return formattedLaps;
                    }
                });

        // ListView(listLaps, ArrayAdapter) のバインド
        final ListView listLaps = (ListView)findViewById(R.id.listLaps);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listLaps.setAdapter(adapter);
        _subscriptionOnCreate.add(new ArrayAdapterBinder(adapter)
                .toItems(formattedLaps) // laps プロパティを 変換して .items へバインド
        );
    }
}
