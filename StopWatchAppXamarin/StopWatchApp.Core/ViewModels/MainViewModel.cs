﻿using System;
using StopWatchApp.Core.Frameworks.Messengers;
using StopWatchApp.Core.Models;
using Reactive.Bindings;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Reactive.Disposables;
using Reactive.Bindings.Extensions;
using System.Diagnostics;
using System.Reactive.Concurrency;

namespace StopWatchApp.Core.ViewModels
{
	public class MainViewModel : IDisposable
	{
		public Messenger Messenger { get; } = new Messenger();

		private readonly CompositeDisposable _subscriptions = new CompositeDisposable();

		// ■ViewModel として公開するプロパティ

		/// <summary> タイマー時間 </summary>
		public ReadOnlyReactiveProperty<string> FormattedTime { get; }
		/// <summary> 実行中かどうか？ </summary>
        public ReadOnlyReactiveProperty<bool> IsRunning { get; }
        /// <summary> フォーマットされた経過時間群 </summary>
        public ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; }
        /// <summary> ミリ秒を表示するか？ </summary>
        public ReadOnlyReactiveProperty<bool> IsVisibleMillis { get; }

		// ■ViewModel として公開するコマンド

		/// <summary> 開始 or 終了 </summary>
		public ReactiveCommand CommandStartOrStop { get; }
		/// <summary> 経過時間の記録 </summary>
		public ReactiveCommand CommandLap { get; }
		/// <summary> ミリ秒以下表示の切り替え </summary>
		public ReactiveCommand CommandToggleVisibleMillis { get; } = new ReactiveCommand(); // いつでも実行可能

		public MainViewModel(IModelPool modelPool)
		{
			var stopWatch = modelPool.StopWatch;

			// ■プロパティの実装
			// StopWatchModel の各プロパティをそのまま公開してるだけ
			IsRunning = stopWatch.IsRunning;
            FormattedLaps = stopWatch.FormattedLaps;
			IsVisibleMillis = stopWatch.IsVisibleMillis;

			// 表示用にthrottleで20ms毎に間引き。View側でやってもよいかも。
            FormattedTime = stopWatch.FormattedTime
                .Throttle(TimeSpan.FromMilliseconds(20), Scheduler.Immediate)
                .ToReadOnlyReactiveProperty();

			// STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
			IsRunning.Where(x => !x)
				.Subscribe(_ => 
				{
					// Toast を表示させる
					Messenger.Send(new ShowToastMessage(
							$"最速ラップ:{stopWatch.FastestLap}, 最遅ラップ:{stopWatch.WorstLap}")); 

					// LapActivity へ遷移させる
					Messenger.Send(new StartViewMessage(typeof(LapViewModel))); // ホントは LapViewModel を指定して画面遷移すべき
				})
				.AddTo(_subscriptions);


			// ■コマンドの実装
			// 開始 or 終了
			CommandStartOrStop = new ReactiveCommand(); // いつでも実行可能 
			CommandStartOrStop.Subscribe(_ => 
				{
					stopWatch.StartOrStop();
				})
				.AddTo(_subscriptions);

			// 経過時間の記録
			CommandLap = IsRunning.ToReactiveCommand(); // 実行中のみ記録可能 
			CommandLap.Subscribe(_ => 
				{
					stopWatch.Lap();
				})
				.AddTo(_subscriptions);

			// ミリ秒以下表示の切り替え
			CommandToggleVisibleMillis = new ReactiveCommand(); // いつでも実行可能 
			CommandToggleVisibleMillis.Subscribe(_ => 
				{
					stopWatch.ToggleVisibleMillis();
				})
				.AddTo(_subscriptions);
		}

		#region IDisposable implementation

		public void Dispose ()
		{
			_subscriptions.Dispose();
			_subscriptions.Clear();
		}

		#endregion
	}
}

