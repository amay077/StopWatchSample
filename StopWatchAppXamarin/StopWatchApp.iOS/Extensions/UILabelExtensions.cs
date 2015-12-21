using System;
using UIKit;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.iOS.Extensions
{
	public static class UILabelExtensions
	{
		public static IDisposable SetBindingToText(this UILabel self, ReactiveProperty<string> prop)
		{
			return prop
				.ObserveOnUIDispatcher()
				.Subscribe(x => self.Text = x);
		}
	}
}

