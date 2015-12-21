using Foundation;
using System;
using System.CodeDom.Compiler;
using UIKit;
using StopWatchApp.Core.ViewModels;
using System.Reactive.Disposables;
using StopWatchApp.Core.Models;
using System.Reactive.Linq;
using Reactive.Bindings;
using StopWatchApp.iOS.Extensions;
using Reactive.Bindings.Extensions;
using System.Linq;

namespace StopWatchApp.iOS.Views
{
	partial class LapViewController : UITableViewController
	{
		private /* readonly */ LapViewModel _viewModel;

		private readonly CompositeDisposable _subscriptionOnLoad = new CompositeDisposable();

		public LapViewController (IntPtr handle) : base (handle)
		{
		}

		public override void ViewDidLoad ()
		{
			base.ViewDidLoad ();

			_viewModel = new LapViewModel(UIApplication.SharedApplication.Delegate as IModelPool);

			// ListView(listLaps, ArrayAdapter) のバインド
			// フォーマットされた経過時間群を表す Observable（time と timeFormat のどちらかが変更されたら更新）
			var formattedLaps = _viewModel.Laps.CombineLatest(
				_viewModel.TimeFormat, 
				(laps, f) => laps.Select((x, i) => $"{i+1}.  {TimeSpan.FromMilliseconds(x).ToString(f)}"))
				.ToReactiveProperty();
			tableLaps.SetBindingToSource(formattedLaps)
				.AddTo(_subscriptionOnLoad);
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
