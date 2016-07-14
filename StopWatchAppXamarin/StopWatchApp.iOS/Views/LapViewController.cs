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
using StopWatchApp.Core.Extensions;

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
            // 番号付きのリストに変換
            var numberedLaps = _viewModel.FormattedLaps.ToNumberedLaps();
			tableLaps
                .SetBindingToSource(numberedLaps)
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
