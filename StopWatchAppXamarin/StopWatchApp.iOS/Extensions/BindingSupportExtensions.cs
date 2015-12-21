using System;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;
using System.Linq.Expressions;
using System.Reactive.Disposables;
using UIKit;
using System.Reactive;
using StopWatchApp.iOS.Extensions.Internal;
using System.Reactive.Linq;

namespace StopWatchApp.iOS.Extensions
{
	public static class BindingSupportExtensions
	{
		/// <summary>
		/// Data binding method.
		/// </summary>
		/// <typeparam name="TView">View type</typeparam>
		/// <typeparam name="TProperty">Property type</typeparam>
		/// <param name="self">View</param>
		/// <param name="propertySelector">Target property selector</param>
		/// <param name="source">Source property</param>
		/// <param name="updateSourceTrigger">Update source trigger</param>
		/// <returns>Data binding token</returns>
		public static IDisposable SetBinding<TView, TProperty>(
			this TView self,
			Expression<Func<TView, TProperty>> propertySelector,
			ReactiveProperty<TProperty> source, Func<TView, IObservable<Unit>> updateSourceTrigger = null)
			where TView : UIView
		{
			var d = new CompositeDisposable();

			bool isUpdating = false;
			string propertyName;
			var setter = AccessorCache<TView>.LookupSet(propertySelector, out propertyName);
			source
				.Where(_ => !isUpdating)
				.Subscribe(x => setter(self, x))
				.AddTo(d);
			if (updateSourceTrigger != null)
			{
				var getter = AccessorCache<TView>.LookupGet(propertySelector, out propertyName);
				updateSourceTrigger(self).Subscribe(_ =>
					{
						isUpdating = true;
						try
						{
							source.Value = getter(self);
						}
						finally
						{
							isUpdating = false;
						}
					}).AddTo(d);
			}

			return d;
		}

		/// <summary>
		/// Data binding method.
		/// </summary>
		/// <typeparam name="TView">View type</typeparam>
		/// <typeparam name="TProperty">Property type</typeparam>
		/// <param name="self">View</param>
		/// <param name="propertySelector">Target property selector</param>
		/// <param name="source">Source property</param>
		/// <returns>Data binding token</returns>
		public static IDisposable SetBinding<TView, TProperty>(
			this TView self,
			Expression<Func<TView, TProperty>> propertySelector,
			ReadOnlyReactiveProperty<TProperty> source)
			where TView : UIView
		{
			var d = new CompositeDisposable();

			bool isUpdating = false;
			string propertyName;
			var setter = AccessorCache<TView>.LookupSet(propertySelector, out propertyName);
			source
				.Where(_ => !isUpdating)
				.Subscribe(x => setter(self, x))
				.AddTo(d);
			return d;
		}

		public static IDisposable SetBindingEx<TView, TProperty>(
			this TView self,
			Action<TView, TProperty> propertySetter,
			ReactiveProperty<TProperty> source)
			where TView : UIView
		{
			var d = new CompositeDisposable();

			bool isUpdating = false;
			source
				.Where(_ => !isUpdating)
				.Subscribe(x => propertySetter(self, x))
				.AddTo(d);
			return d;
		}

		/// <summary>
		/// Command binding method.
		/// </summary>
		/// <typeparam name="T">Command type.</typeparam>
		/// <param name="self">IObservable</param>
		/// <param name="command">Command</param>
		/// <returns>Command binding token</returns>
		public static IDisposable SetCommand<T>(this IObservable<T> self, ReactiveCommand<T> command) =>
			self.Where(_ => command.CanExecute())
				.Subscribe(x => command.Execute(x));

		/// <summary>
		/// Command binding method.
		/// </summary>
		/// <typeparam name="T">IObservable type</typeparam>
		/// <param name="self">IObservable</param>
		/// <param name="command">Command</param>
		/// <returns>Command binding token</returns>
		public static IDisposable SetCommand<T>(this IObservable<T> self, ReactiveCommand command) =>
			self.Where(_ => command.CanExecute())
				.Subscribe(x => command.Execute());
	}
}

