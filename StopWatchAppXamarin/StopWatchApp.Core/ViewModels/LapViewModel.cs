using System;
using StopWatchApp.Core.Frameworks.Messengers;
using StopWatchApp.Core.Models;
using Reactive.Bindings;
using System.Collections.Generic;
using System.Reactive.Linq;

namespace StopWatchApp.Core.ViewModels
{
	public class LapViewModel : IDisposable
	{
		/// <summary>経過時間群</summary>
		public ReactiveProperty<IList<long>> Laps { get; }
		/// <summary>時間の表示フォーマット</summary>
		public ReactiveProperty<string> TimeFormat { get; }

		public LapViewModel (IModelPool modelPool)
		{
			var stopWatch = modelPool.StopWatch;

			Laps = stopWatch.Laps;
			// ミリ秒以下表示有無に応じて、format書式文字列を切り替え（これはModelでやるべき？）
			TimeFormat = stopWatch.IsVisibleMillis.Select(x => x ? "mm:ss.SSS" : "mm:ss").ToReactiveProperty();
		}

		#region IDisposable implementation
		public void Dispose ()
		{
		}
		#endregion
	}

}

