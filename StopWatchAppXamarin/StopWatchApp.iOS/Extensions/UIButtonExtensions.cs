using System;
using UIKit;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.iOS.Extensions
{
	public static class UIButtonExtensions
	{
		public static IDisposable SetBindingToText(this UIButton self, IObservable<string> prop)
		{
			return prop
				.ObserveOnUIDispatcher()
				.Subscribe(x => self.SetTitle(x, UIControlState.Normal));
		}
	}
}

