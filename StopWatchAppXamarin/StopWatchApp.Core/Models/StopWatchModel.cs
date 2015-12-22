using System;
using Reactive.Bindings;
using System.Collections.Generic;
using System.Reactive.Concurrency;
using System.Reactive.Linq;
using System.Linq;

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

		// Model として公開するプロパティ
        public ReadOnlyReactiveProperty<long> Time  { get; } // タイマー時間
        public ReadOnlyReactiveProperty<bool> IsRunning  { get; } // 実行中か？
        public ReadOnlyReactiveProperty<IList<long>> Laps  { get; } // 経過時間群
        public ReadOnlyReactiveProperty<bool> IsVisibleMillis  { get; }// ミリ秒表示するか？

		// タイマーの購読状況
		private IDisposable _timerSubscription = null;

        public StopWatchModel()
        {
            Time = _time.ToReadOnlyReactiveProperty();
            IsRunning = _isRunning.ToReadOnlyReactiveProperty();
            Laps = _laps.ToReadOnlyReactiveProperty();
            IsVisibleMillis = _isVisibleMillis.ToReadOnlyReactiveProperty();
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
			var laps = Laps.Value;
			var newLaps = new List<long>();

			newLaps.AddRange(laps);

			var totalLap = 0L;
			foreach (var lap in laps) {
				totalLap += lap;
			}

			newLaps.Add(Time.Value - totalLap);

			_laps.Value = newLaps;
		}

		/// <summary>
		/// 最速ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい)
		/// </summary>
		public long? FastestLap  {
			get 
			{
				var laps = Laps.Value;
				if (laps.Count == 0) {
					return null; // nullを使うことを許したまえ
				} else {
					return laps.Min(); // LINQ便利
				}
			}
		}

		/// <summary>
		/// 最遅ラップを取得(返り値でなく、Observableなプロパティにした方がホントはよい) */
		/// </summary>
		public long? WorstLap  {
			get 
			{
				var laps = Laps.Value;
				if (laps.Count == 0) {
					return null; // nullを使うことを許したまえ
				} else {
					return laps.Max(); // LINQ便利
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

