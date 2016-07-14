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
        public ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; }
		/// <summary>時間の表示フォーマット</summary>
        public ReadOnlyReactiveProperty<string> TimeFormat { get; }

		public LapViewModel (IModelPool modelPool)
		{
			var stopWatch = modelPool.StopWatch;

			FormattedLaps = stopWatch.FormattedLaps;
		}

		#region IDisposable implementation
		public void Dispose ()
		{
		}
		#endregion
	}

}

