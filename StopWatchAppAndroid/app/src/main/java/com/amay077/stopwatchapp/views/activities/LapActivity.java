package com.amay077.stopwatchapp.views.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.amay077.stopwatchapp.App;
import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;

import javax.inject.Inject;

public class LapActivity extends AppCompatActivity {

    @Inject
    /*private final*/ LapViewModel _viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inject by Dagger2
        final App app = (App) getApplication();
        app.getApplicationComponent().inject(this);

        ActivityLapBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_lap);
        binding.setViewModel(_viewModel);
    }

    @Override
    protected void onDestroy() {
        _viewModel.dispose();
        super.onDestroy();
    }
}
