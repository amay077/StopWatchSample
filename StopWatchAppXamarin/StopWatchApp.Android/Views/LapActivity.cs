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
using StopWatchApp.Core.Extensions;

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
            // 番号付きのリストに変換
            var numberedLaps = _viewModel.FormattedLaps.ToNumberedLaps();

			var listLaps = FindViewById<ListView>(Resource.Id.listLaps);
			var listAdapter = new ArrayAdapter(this, global::Android.Resource.Layout.SimpleListItem1);
			listLaps.Adapter = listAdapter;
			listAdapter
				.SetBinding(numberedLaps)
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

