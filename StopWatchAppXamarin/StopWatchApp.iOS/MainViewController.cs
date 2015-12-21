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

namespace StopWatchApp.iOS
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
			labelTime.SetBindingToText(_viewModel.Time
				.CombineLatest(
					_viewModel.TimeFormat,
					(t, f) => TimeSpan.FromMilliseconds(t).ToString(f))
				.ObserveOnUIDispatcher()
				.ToReactiveProperty())
				.AddTo(_subscriptionOnLoad);

			// UIButton(buttonStartStop) のバインド
			buttonStartStop.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandStartOrStop)
				.AddTo(_subscriptionOnLoad);
			buttonStartStop.SetBindingToEnabled(
				_viewModel.CommandStartOrStop.CanExecuteChangedAsObservable()
				.Select(_=>_viewModel.CommandStartOrStop.CanExecute()))
				.AddTo(_subscriptionOnLoad);
			buttonStartStop
				.SetBindingToText(
					_viewModel.IsRunning.Select(x => x ? "STOP" : "START")
					.ObserveOnUIDispatcher()
					.ToReactiveProperty())
				.AddTo(_subscriptionOnLoad);
			
			// UIButton(buttonLap) のバインド
			buttonLap
				.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandLap)
				.AddTo(_subscriptionOnLoad);

			// UISwitch(switchVisibleMillis) のバインド
			switchVisibleMillis
				.SetBindingToChecked(_viewModel.IsVisibleMillis)
				.AddTo(_subscriptionOnLoad);
			switchVisibleMillis.TouchUpInsideAsObservable()
				.SetCommand(_viewModel.CommandToggleVisibleMillis)
				.AddTo(_subscriptionOnLoad);

			// ■ViewModel からの Message の受信

			// 画面遷移のメッセージ受信
			_viewModel.Messenger.Register(typeof(StartViewMessage).Name, message => InvokeOnMainThread(() => {
			}));

			// トースト表示のメッセージ受信
			_viewModel.Messenger.Register (typeof(ShowToastMessage).Name, message => InvokeOnMainThread(() => {
				var m = message as ShowToastMessage;
//				Toast.MakeText(this, m.Text, ToastLength.Long).Show();
			}));
			
		}
	}
}
