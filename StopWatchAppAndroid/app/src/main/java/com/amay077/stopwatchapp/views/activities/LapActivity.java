package com.amay077.stopwatchapp.views.activities;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapItem;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

import java.util.List;

import rx.functions.Action0;

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

        // TODO オレオレBindingなのが気に入らない
        bindAdapter(lapAdapter, _viewModel.formattedLaps);
    }

    private void bindAdapter(final LapAdapter adapter, final ObservableField<List<LapItem>> observableField) {
        final android.databinding.Observable.OnPropertyChangedCallback formattedLapsChangedHandler =
                new android.databinding.Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                        adapter.clear();
                        adapter.addAll(_viewModel.formattedLaps.get());
                    }
                };
        observableField.addOnPropertyChangedCallback(formattedLapsChangedHandler);
        adapter.clear();
        adapter.addAll(observableField.get());

        _removeCallback = new Action0() {
            @Override
            public void call() {
                observableField.removeOnPropertyChangedCallback(formattedLapsChangedHandler);
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
