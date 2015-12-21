using System;
using System.Reactive;
using System.Reactive.Linq;
using System.Reactive.Disposables;
using Reactive.Bindings;
using Reactive.Bindings.Binding;
using Reactive.Bindings.Extensions;
using Java.Text;
using Java.Util;
using Android.App;
using Android.Widget;
using Android.OS;
using StopWatchApp.Core;
using StopWatchApp.Core.ViewModels;
using StopWatchApp.Core.Models;

using Locale = Java.Util.Locale;
using Android.Content;
using StopWatchApp.Core.Frameworks.Messengers;
using System.Collections.Generic;
using StopWatchApp.Android.Extensions;
using System.Linq;

namespace StopWatchApp.Android.Views
{
	[Activity (Label = "StopWatchAppXamarin", MainLauncher = true, Icon = "@mipmap/icon")]
	public class MainActivity : Activity
	{
		private /* readonly */ MainViewModel _viewModel;

		private readonly CompositeDisposable _subscriptionOnCreate = new CompositeDisposable();

		protected override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			_viewModel = new MainViewModel(this.Application as IModelPool);

			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.activity_main);

			// TextView(textTime) のバインド
			FindViewById<TextView>(Resource.Id.textTime)
				.SetBinding(v => v.Text, 
					// フォーマットされた時間を表す Observable（time と timeFormat のどちらかが変更されたら更新）
					_viewModel.Time
					.CombineLatest(
						_viewModel.TimeFormat,
						(t, f) => TimeSpan.FromMilliseconds(t).ToString(@"mm\:ss\.fff"))
					.ObserveOnUIDispatcher()
					.ToReactiveProperty())
				.AddTo(_subscriptionOnCreate);

			// Button(buttonStartStop) のバインド
			var buttonStartStop = FindViewById<Button>(Resource.Id.buttonStartStop);
			buttonStartStop.ClickAsObservable()
				.SetCommand(_viewModel.CommandStartOrStop)
				.AddTo(_subscriptionOnCreate);
			buttonStartStop
				.SetBinding(v => v.Text, 
				_viewModel.IsRunning.Select(x => x ? "STOP" : "START")
					.ObserveOnUIDispatcher()
					.ToReactiveProperty())
				.AddTo(_subscriptionOnCreate);

			// Button(buttonLap) のバインド
			FindViewById<Button> (Resource.Id.buttonLap)
				.ClickAsObservable().SetCommand(_viewModel.CommandLap)
				.AddTo(_subscriptionOnCreate);

			// Switch(switchVisibleMillis) のバインド
			var switchVisibleMillis = FindViewById<Switch>(Resource.Id.switchVisibleMillis);
			switchVisibleMillis
				.SetBinding(v => v.Checked, _viewModel.IsVisibleMillis
					.ObserveOnUIDispatcher()
					.ToReactiveProperty())
				.AddTo(_subscriptionOnCreate);
			switchVisibleMillis.ClickAsObservable()
				.SetCommand(_viewModel.CommandToggleVisibleMillis)
				.AddTo(_subscriptionOnCreate);

			// ListView(listLaps, ArrayAdapter) のバインド
			// フォーマットされた経過時間群を表す Observable（time と timeFormat のどちらかが変更されたら更新）
			var formattedLaps = _viewModel.Laps.CombineLatest(
				_viewModel.TimeFormat, (laps, f) => {
					var sdf = new SimpleDateFormat(f, Locale.Default);
					return laps.Select((x, i) => $"{i+1}.  {sdf.Format(new Date(x))}");
				})
				.ToReactiveProperty();
			
			var listLaps = FindViewById<ListView>(Resource.Id.listLaps);
			var listAdapter = new ArrayAdapter(this, global::Android.Resource.Layout.SimpleListItem1);
			listLaps.Adapter = listAdapter;
			listAdapter
				.SetBinding(formattedLaps)
				.AddTo(_subscriptionOnCreate);
			

			// ■ViewModel からの Message の受信

			// 画面遷移のメッセージ受信
			_viewModel.Messenger.Register(typeof(StartViewMessage).Name, message => RunOnUiThread(() => {
				var app = this.ApplicationContext as App;
				var m = message as StartViewMessage;

				if (app.VmToActivityTypes.ContainsKey(m.ViewModelType)) {
					var intent = new Intent(this, app.VmToActivityTypes[m.ViewModelType]);
					this.StartActivity(intent);
				}
			}));

			// トースト表示のメッセージ受信
			_viewModel.Messenger.Register (typeof(ShowToastMessage).Name, message => RunOnUiThread (() => {
				var m = message as ShowToastMessage;
				Toast.MakeText(this, m.Text, ToastLength.Long).Show();
			}));
		}

		protected override void OnDestroy ()
		{
			// unsubscribe しないと Activity がリークするよ
			_subscriptionOnCreate.Dispose();
			_subscriptionOnCreate.Clear();

			if (_viewModel != null) {
				_viewModel.Dispose ();
			}

			base.OnDestroy ();
		}
	}
}


