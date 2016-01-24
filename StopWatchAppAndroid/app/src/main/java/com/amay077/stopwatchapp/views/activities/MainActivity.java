package com.amay077.stopwatchapp.views.activities;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityMainBinding;
import com.amay077.stopwatchapp.frameworks.messengers.Message;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.viewmodel.LapItem;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private /* final */  MainViewModel _viewModel;
    private CompositeSubscription _subscriptions = new CompositeSubscription();
    private Action0 _removeCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        _viewModel = new MainViewModel(this.getApplicationContext());
        binding.setViewModel(_viewModel);

        final LapAdapter lapAdapter = new LapAdapter(this);
        binding.listLaps.setAdapter(lapAdapter);

        // TODO オレオレBindingなのが気に入らない
        bindAdapter(lapAdapter, _viewModel.formattedLaps);

        // ■ViewModel からの Message の受信

        // 画面遷移のメッセージ受信
        _subscriptions.add(
                _viewModel.messenger.register(StartActivityMessage.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<StartActivityMessage>() {
                            @Override
                            public void call(StartActivityMessage m) {
                                Intent intent = new Intent(MainActivity.this, m.activityClass);
                                MainActivity.this.startActivity(intent);
                            }
                        }));

        // トースト表示のメッセージ受信
        _subscriptions.add(
                _viewModel.messenger.register(ShowToastMessages.class)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<ShowToastMessages>() {
                            @Override
                            public void call(ShowToastMessages m) {
                                Toast.makeText(MainActivity.this, m.text, Toast.LENGTH_LONG).show();
                            }
                        })
        );
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
