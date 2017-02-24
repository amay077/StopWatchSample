package com.amay077.stopwatchapp.views.activities;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;import android.widget.ListView;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;

public class LapActivity extends AppCompatActivity {

    private /* final */ LapViewModel _viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _viewModel = new LapViewModel(this.getApplicationContext());

        ActivityLapBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_lap);
        binding.setViewModel(_viewModel);
    }

    @Override
    protected void onDestroy() {
        _viewModel.dispose();
        super.onDestroy();
    }
}
