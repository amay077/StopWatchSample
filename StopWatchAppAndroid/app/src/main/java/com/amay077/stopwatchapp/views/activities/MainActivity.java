package com.amay077.stopwatchapp.views.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.Observable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityMainBinding;
import com.amay077.stopwatchapp.frameworks.binders.ArrayAdapterBinder;
import com.amay077.stopwatchapp.frameworks.messengers.Message;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

import rx.functions.Action;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private /* final */  MainViewModel _viewModel;

    private Action0 _removeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        _viewModel = new MainViewModel(this.getApplicationContext());
        binding.setViewModel(_viewModel);

        final LapAdapter lapAdapter = new LapAdapter(this);
        binding.listLaps.setAdapter(lapAdapter);

        final Observable.OnPropertyChangedCallback formattedLapsChangedHandler = new Observable.OnPropertyChangedCallback() {
            @Override
            public void onPropertyChanged(Observable sender, int propertyId) {
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

        // ■ViewModel からの Message の受信

        // 画面遷移のメッセージ受信
        _viewModel.messenger.register(StartActivityMessage.class.getName(), new Action1<Message>() {
            @Override
            public void call(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final StartActivityMessage m = (StartActivityMessage) message;
                        Intent intent = new Intent(MainActivity.this, m.activityClass);
                        MainActivity.this.startActivity(intent);
                    }
                });
            }
        });

        // トースト表示のメッセージ受信
        _viewModel.messenger.register(ShowToastMessages.class.getName(), new Action1<Message>() {
            @Override
            public void call(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final ShowToastMessages m = (ShowToastMessages) message;
                        Toast.makeText(MainActivity.this, m.text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

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
