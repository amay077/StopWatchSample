using System;
using Android.App;
using StopWatchApp.Core.ViewModels;
using System.Reactive.Disposables;
using Android.OS;
using StopWatchApp.Core.Models;
using Java.Text;
using System.Linq;
using System.Reactive.Linq;
using Locale = Java.Util.Locale;
using Java.Util;
using Reactive.Bindings;
using Android.Widget;
using Reactive.Bindings.Extensions;
using StopWatchApp.Android.Extensions;

namespace StopWatchApp.Android.Views
{
	[Activity (Label = "StopWatchAppXamarin")]
	public class LapActivity : Activity
	{
		private /* readonly */ LapViewModel _viewModel;

		private readonly CompositeDisposable _subscriptionOnCreate = new CompositeDisposable();

		protected override void OnCreate (Bundle savedInstanceState)
		{
			base.OnCreate (savedInstanceState);

			_viewModel = new LapViewModel(this.Application as IModelPool);

			// Set our view from the "main" layout resource
			SetContentView (Resource.Layout.activity_lap);

			// ListView(listLaps, ArrayAdapter) のバインド
			// フォーマットされた経過時間群を表す Observable（time と timeFormat のどちらかが変更されたら更新）
			var formattedLaps = _viewModel.Laps.CombineLatest(
				_viewModel.TimeFormat, 
				(laps, f) => laps.Select((x, i) => $"{i+1}.  {TimeSpan.FromMilliseconds(x).ToString(f)}"))
				.ToReactiveProperty();

			var listLaps = FindViewById<ListView>(Resource.Id.listLaps);
			var listAdapter = new ArrayAdapter(this, global::Android.Resource.Layout.SimpleListItem1);
			listLaps.Adapter = listAdapter;
			listAdapter
				.SetBinding(formattedLaps)
				.AddTo(_subscriptionOnCreate);
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
	}}

