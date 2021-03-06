﻿using System;
using System.Collections.Generic;
using Reactive.Bindings;

namespace StopWatchAppXamarinForms.Models
{
    public interface IStopWatchModel : IDisposable
    {
        ReadOnlyReactiveProperty<string> FormattedTime { get; } // フォーマットされたタイマー時間
        ReadOnlyReactiveProperty<bool> IsRunning { get; } // 実行中か？
        ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; } // フォーマットされた経過時間群
        ReadOnlyReactiveProperty<string> FormattedFastestLap { get; }
        ReadOnlyReactiveProperty<string> FormattedWorstLap { get; }

        ReactiveProperty<bool> IsVisibleMillis { get; }// ミリ秒表示するか？

        void StartOrStop();
        void Lap();
        void ToggleVisibleMillis();
    }
}