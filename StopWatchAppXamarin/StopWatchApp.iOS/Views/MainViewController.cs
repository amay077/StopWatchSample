using Foundation;
using System;
using System.CodeDom.Compiler;
using UIKit;
using StopWatchApp.iOS.Extensions;
using StopWatchApp.Core.ViewModels;
using StopWatchApp.Core.Models;
using System.Reactive.Linq;
using Reactive.Bindings.Extensions;
using Reactive.Bindings;
using System.Reactive.Disposables;
using System.Diagnostics;
using StopWatchApp.Core.Frameworks.Messengers;
using System.Linq;
using Xamarin.Controls;
using StopWatchApp.Core.Extensions;

namespace StopWatchApp.iOS.Views
{
	partial class MainViewController : UIViewController
	{
		private /* readonly */ MainViewModel _viewModel;

		private readonly CompositeDisposable _subscriptionOnLoad = new CompositeDisposable();

		public MainViewController (IntPtr handle) : base (handle)
		{
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();

			_viewModel = new MainViewModel(UIApplication.SharedApplication.Delegate as IModelPool);

			// UILabel(labelTime) のバインド
            labelTime.SetBinding(v => v.Text, _viewModel.FormattedTime.ObserveOnUIDispatcher().ToReactiveProperty())
				.AddTo(_subscriptionOnLoad);

			// UIButton(buttonStartStop) のバインド
			buttonStartStop.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandStartOrStop)
				.AddTo(_subscriptionOnLoad);
			buttonStartStop
				.SetBinding(
					(v, value) => v.SetTitle(value, UIControlState.Normal),
					_viewModel.IsRunning.Select(x => x ? "STOP" : "START")
					.ObserveOnUIDispatcher()
					.ToReactiveProperty())
				.AddTo(_subscriptionOnLoad);
			
			// UIButton(buttonLap) のバインド
			buttonLap
				.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandLap)
				.AddTo(_subscriptionOnLoad);
			buttonLap.SetBinding(
				v=>v.Enabled, _viewModel.IsRunning)
				.AddTo(_subscriptionOnLoad);

			// UISwitch(switchVisibleMillis) のバインド
			switchVisibleMillis
				.SetBinding(v=>v.On, _viewModel.IsVisibleMillis)
				.AddTo(_subscriptionOnLoad);
			switchVisibleMillis.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandToggleVisibleMillis)
				.AddTo(_subscriptionOnLoad);


            // ListView(listLaps, ArrayAdapter) のバインド
            // 番号付きのリストに変換
            var numberedLaps = _viewModel.FormattedLaps;
            tableLaps
                .SetBindingToSource(numberedLaps)
                .AddTo(_subscriptionOnLoad);

            // ■ViewModel からの Message の受信

            // 画面遷移のメッセージ受信
            _viewModel.Messenger.Register(typeof(StartViewMessage).Name, message => InvokeOnMainThread(() => {
				PerformSegue("goto_lap", this);
			}));

			// トースト表示のメッセージ受信
			_viewModel.Messenger.Register (typeof(ShowToastMessage).Name, message => InvokeOnMainThread(() => {
				var m = message as ShowToastMessage;
				AlertCenter.Default.PostMessage ("Notification", m.Text);
			}));
			
		}

		public override void ViewDidUnload ()
		{
			// unsubscribe しないと Activity がリークするよ
			_subscriptionOnLoad.Dispose();
			_subscriptionOnLoad.Clear();

			if (_viewModel != null) {
				_viewModel.Dispose ();
			}
			base.ViewDidUnload();
		}
	}
}
