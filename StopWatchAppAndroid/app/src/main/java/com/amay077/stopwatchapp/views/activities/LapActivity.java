package com.amay077.stopwatchapp.views.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityLapBinding;
import com.amay077.stopwatchapp.databinding.ActivityMainBinding;
import com.amay077.stopwatchapp.frameworks.binders.ArrayAdapterBinder;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.functions.Action0;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class LapActivity extends AppCompatActivity {

    private /* final */ LapViewModel _viewModel;

    private Action0 _removeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _viewModel = new LapViewModel(this.getApplicationContext());

        ActivityLapBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_lap);

        final LapAdapter lapAdapter = new LapAdapter(this);
        binding.listLaps.setAdapter(lapAdapter);

        final android.databinding.Observable.OnPropertyChangedCallback formattedLapsChangedHandler =
                new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                lapAdapter.clear();
                lapAdapter.addAll(_viewModel.formattedLaps.get());
            }
        };
        _viewModel.formattedLaps.addOnPropertyChangedCallback(formattedLapsChangedHandler);

        _removeCallback = new Action0() {
            @Override
            public void call() {
                _viewModel.formattedLaps.removeOnPropertyChangedCallback(formattedLapsChangedHandler);
            }
        };
    }

    @Override
    protected void onDestroy() {

        if (_removeCallback != null) {
            _removeCallback.call();
        }

        _viewModel.unsubscribe();
        super.onDestroy();
    }
}
