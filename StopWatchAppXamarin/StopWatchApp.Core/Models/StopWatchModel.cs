using System;
using Reactive.Bindings;
using System.Collections.Generic;
using System.Reactive.Concurrency;
using System.Reactive.Linq;
using System.Linq;
using System.Diagnostics;

namespace StopWatchApp.Core.Models
{
	/// <summary>
	/// ストップウォッチの機能を実装したロジッククラス
	/// </summary>
	public class StopWatchModel : IDisposable
	{
        // ストップウォッチの状態を更新＆通知するための ReactiveProperty 群(要は Subject)
        private readonly ReactiveProperty<long> _time = new ReactiveProperty<long>(0L); // タイマー時間
        private readonly ReactiveProperty<bool> _isRunning = new ReactiveProperty<bool>(false); // 実行中か？
        private readonly ReactiveProperty<IList<long>> _laps = new ReactiveProperty<IList<long>>(new List<long>()); // 経過時間群
        private readonly ReactiveProperty<bool> _isVisibleMillis = new ReactiveProperty<bool>(true); // ミリ秒表示するか？
        private readonly ReactiveProperty<string> _timeFormat;

        // Model として公開するプロパティ
        public ReadOnlyReactiveProperty<string> FormattedTime { get; } // フォーマットされたタイマー時間
        public ReadOnlyReactiveProperty<bool> IsRunning  { get; } // 実行中か？
        public ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; } // フォーマットされた経過時間群
        public ReadOnlyReactiveProperty<bool> IsVisibleMillis  { get; }// ミリ秒表示するか？

		// タイマーの購読状況
		private IDisposable _timerSubscription = null;

        public StopWatchModel()
        {
            Debug.WriteLine("StopWatchModel ctor");

            IsRunning = _isRunning.ToReadOnlyReactiveProperty();
            IsVisibleMillis = _isVisibleMillis.ToReadOnlyReactiveProperty();

            _timeFormat = IsVisibleMillis
                .Select(v => v ? @"mm\:ss\.fff" : @"mm\:ss")
                .ToReactiveProperty();

            FormattedTime = _time.CombineLatest(_timeFormat, (time, format) =>
                    TimeSpan.FromMilliseconds(time).ToString(format))
                .ToReadOnlyReactiveProperty();

            FormattedLaps = _laps.CombineLatest(_timeFormat,
                (laps, f) => laps.Select((x, i) => TimeSpan.FromMilliseconds(x).ToString(f)))
                .ToReadOnlyReactiveProperty();
        }

		/// <summary>
		/// 計測を開始または終了する(time プロパティの更新を開始する)
		/// </summary>
		public void StartOrStop() {
			if (!IsRunning.Value) {
				Start();
			} else {
				Stop();
			}
		}

		private void Start() {
			if (IsRunning.Value) {
				Stop();
			}

			// 開始時に Laps をクリア、実行中フラグをON
			// RxJava の compose がないので「購読が開始された時」でないのが微妙…
			_laps.Value = new List<long>();;
			_isRunning.Value = true;
			var now = DateTime.Now;

			_timerSubscription =
				Observable.Interval(TimeSpan.FromMilliseconds(10), Scheduler.Default)
					.Subscribe(time => {
						_time.Value = Convert.ToInt64((DateTime.Now - now).TotalMilliseconds);
					}); // タイマー値を通知
		}

		private void Stop() {
			if (_timerSubscription != null) {
				_timerSubscription.Dispose();
				_timerSubscription = null;
			}

			// 実行終了を通知
			_isRunning.Value = false;
		}

		/// <summary>
		/// 経過時間を記録する(経過時間を laps プロパティに追加する)
		/// </summary>
		public void Lap() {
			var laps = _laps.Value;
			var newLaps = new List<long>();

			newLaps.AddRange(laps);

			var totalLap = 0L;
			foreach (var lap in laps) {
				totalLap += lap;
			}

			newLaps.Add(_time.Value - totalLap);

			_laps.Value = newLaps;
		}

		/// <summary>
		/// 最速ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい)
		/// </summary>
		public string FastestLap  {
			get 
			{
				var laps = _laps.Value;
				if (laps.Count == 0) {
                    return TimeSpan.FromMilliseconds(_time.Value).ToString(_timeFormat.Value);
				} else {
                    // LINQ便利
                    return  TimeSpan.FromMilliseconds(laps.Min()).ToString(_timeFormat.Value); 
				}
			}
		}

		/// <summary>
		/// 最遅ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい) */
		/// </summary>
		public string WorstLap  {
			get 
			{
				var laps = _laps.Value;
				if (laps.Count == 0) {
                    return TimeSpan.FromMilliseconds(_time.Value).ToString(_timeFormat.Value);
                } else {
                    // LINQ便利
                    return TimeSpan.FromMilliseconds(laps.Max()).ToString(_timeFormat.Value);
                }
			}
		}

		/// <summary>
		/// 小数点以下の表示有無を切り替える（isVisibleMillis プロパティを toggle する）
		/// </summary>
		public void ToggleVisibleMillis() {
			_isVisibleMillis.Value = !_isVisibleMillis.Value;
		}

		#region IDisposable implementation

		public void Dispose ()
		{
			Stop();
		}

		#endregion
	}

}

