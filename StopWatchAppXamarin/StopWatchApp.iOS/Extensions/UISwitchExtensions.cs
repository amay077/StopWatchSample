using System;
using Reactive.Bindings;
using UIKit;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.iOS.Extensions
{
	public static class UISwitchExtensions
	{
		public static IDisposable SetBindingToChecked(this UISwitch self, ReactiveProperty<bool> prop)
		{
			return prop
				.ObserveOnUIDispatcher()
				.Subscribe(x => self.On = x);
		}
	}
}

