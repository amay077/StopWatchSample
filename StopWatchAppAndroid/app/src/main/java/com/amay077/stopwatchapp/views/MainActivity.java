package com.amay077.stopwatchapp.views;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.amay077.stopwatchapp.R;
import com.amay077.stopwatchapp.databinding.ActivityMainBinding;
import com.amay077.stopwatchapp.frameworks.binders.ArrayAdapterBinder;
import com.amay077.stopwatchapp.frameworks.binders.ButtonBinder;
import com.amay077.stopwatchapp.frameworks.binders.SwitchBinder;
import com.amay077.stopwatchapp.frameworks.binders.TextViewBinder;
import com.amay077.stopwatchapp.frameworks.messengers.Message;
import com.amay077.stopwatchapp.frameworks.messengers.ShowToastMessages;
import com.amay077.stopwatchapp.frameworks.messengers.StartActivityMessage;
import com.amay077.stopwatchapp.viewmodel.MainViewModel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.subscriptions.CompositeSubscription;

public class MainActivity extends AppCompatActivity {

    private /* final */  MainViewModel _viewModel;

    private final CompositeSubscription _subscriptionOnCreate = new CompositeSubscription();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final ActivityMainBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        _viewModel = new MainViewModel(this.getApplicationContext());
        binding.setViewModel(_viewModel);

        // ListView(listLaps, ArrayAdapter) のバインド
        final ListView listLaps = (ListView)findViewById(R.id.listLaps);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        listLaps.setAdapter(adapter);
        _subscriptionOnCreate.add(new ArrayAdapterBinder(adapter)
                .toItems(_viewModel.formattedLaps) // laps プロパティを 変換して .items へバインド
        );

        // ■ViewModel からの Message の受信

        // 画面遷移のメッセージ受信
        _viewModel.messenger.register(StartActivityMessage.class.getName(), new Action1<Message>() {
            @Override
            public void call(final Message message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final StartActivityMessage m = (StartActivityMessage)message;
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
                        final ShowToastMessages m = (ShowToastMessages)message;
                        Toast.makeText(MainActivity.this, m.text, Toast.LENGTH_LONG).show();
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        // unsubscribe しないと Activity がリークするよ
        _subscriptionOnCreate.unsubscribe();
        _subscriptionOnCreate.clear();

        _viewModel.unsubscribe();
        super.onDestroy();
    }
}
