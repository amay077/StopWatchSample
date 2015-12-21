using System;
using UIKit;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using System.Reactive;
using System.Reactive.Disposables;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.iOS.Extensions
{
	public static class UIControlExtensions
	{
		public static IObservable<EventArgs> TouchUpInsideAsObservable(this UIControl self)
		{
			return Observable.Create<EventArgs>(observer => {

				EventHandler h = (_, e) => {
					observer.OnNext(e);
				};

				self.TouchUpInside += h;

				return Disposable.Create(() => {
					self.TouchUpInside -= h;
				});
			});
		}
	}
}

