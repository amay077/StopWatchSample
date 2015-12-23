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
        public static IDisposable SetBinding<TView, TProperty>(
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
	}
}

