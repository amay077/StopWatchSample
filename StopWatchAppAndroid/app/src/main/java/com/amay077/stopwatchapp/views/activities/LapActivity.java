package com.amay077.stopwatchapp.views.activities;

import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityLapBinding;
import com.amay077.stopwatchapp.viewmodel.LapViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

public class LapActivity extends AppCompatActivity {

    private /* final */ LapViewModel _viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        _viewModel = new LapViewModel(this.getApplicationContext());

        ActivityLapBinding binding =  DataBindingUtil.setContentView(this, R.layout.activity_lap);
        binding.setViewModel(_viewModel);
    }

    /**
     * ListView と ViewModel のカスタムバインディング
     *
     * TODO 本当は viewModel.formattedLaps とバインドしたい
     */
    @BindingAdapter("formattedLaps")
    public static void setFormattedLaps(ListView listView, final LapViewModel viewModel) {
        final LapAdapter adapter = new LapAdapter(listView.getContext());
        listView.setAdapter(adapter);

        viewModel.formattedLaps.addOnPropertyChangedCallback(new android.databinding.Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(android.databinding.Observable sender, int propertyId) {
                adapter.clear();
                adapter.addAll(viewModel.formattedLaps.get());
            }
        });

        // バインド時に値を更新
        adapter.clear();
        adapter.addAll(viewModel.formattedLaps.get());
    }

    @Override
    protected void onDestroy() {
        _viewModel.dispose();
        super.onDestroy();
    }
}
