using System;
using Android.Widget;
using System.Collections.Generic;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.Android.Extensions
{
	public static class ArrayAdapterExtensions
	{
		public static IDisposable SetBinding(this ArrayAdapter self, ReactiveProperty<IEnumerable<string>> prop)
		{
			return prop.ObserveOnUIDispatcher()
				.Subscribe(items => {
					self.Clear();
					foreach (var item in items) {
						self.Add(item);
					}
				});
		}
	}
}

