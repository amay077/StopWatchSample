package com.amay077.stopwatchapp.views.activities;

import android.content.Intent;
import android.databinding.BindingAdapter;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityMainBinding;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;
import com.amay077.stopwatchapp.views.adapters.LapAdapter;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private /* final */  MainViewModel _viewModel;
    private CompositeSubscription _subscriptions = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        _viewModel = new MainViewModel(this.getApplicationContext());
        binding.setViewModel(_viewModel);

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

    /**
     * ListView と ViewModel のカスタムバインディング
     *
     * TODO 本当は viewModel.formattedLaps とバインドしたい
     */
    @BindingAdapter("formattedLaps")
    public static void setFormattedLaps(ListView listView, final MainViewModel viewModel) {
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
        _viewModel.unsubscribe();
        super.onDestroy();
    }
}
