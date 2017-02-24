using System;
using System.Collections.Generic;
using Reactive.Bindings;
using StopWatchAppXamarinForms.Models;

namespace StopWatchAppXamarinForms.ViewModels
{
    public class LapViewModel
    {
        /// <summary> フォーマットされた経過時間群 </summary>
        public ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; }

        public LapViewModel(IStopWatchModel stopWatch)
        {
            // ■プロパティの実装
            // StopWatchModel の各プロパティをそのまま公開してるだけ
            FormattedLaps = stopWatch.FormattedLaps;
        }
    }
}
