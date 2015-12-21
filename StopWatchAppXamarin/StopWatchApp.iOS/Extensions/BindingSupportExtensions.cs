using System;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;

namespace StopWatchApp.iOS.Extensions
{
	public static class BindingSupportExtensions
	{
		public static IDisposable SetCommand<T> (this IObservable<T> self, ReactiveCommand command)
		{
			return self
				.ObserveOnUIDispatcher()
				.Subscribe(_ => {
					if (command.CanExecute()) { 
						command.Execute(); 
					}
				});
		}
	}
}

