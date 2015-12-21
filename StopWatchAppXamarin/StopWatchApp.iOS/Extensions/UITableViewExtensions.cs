using System;
using UIKit;
using System.Collections.Generic;
using Reactive.Bindings.Extensions;
using System.Linq;

namespace StopWatchApp.iOS.Extensions
{
	public static class UITableViewExtensions
	{
		public static IDisposable SetBindingToSource(this UITableView self, IObservable<IEnumerable<string>> prop)
		{
			return prop
				.ObserveOnUIDispatcher()
				.Subscribe (items => { 
					self.Source = new LapTableSource(items.ToArray()); // FIXME new し直す必要は無いな
					self.ReloadData();
				});
		}
	}
}

