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
		// Model として公開するプロパティ
		// ストップウォッチの状態を更新＆通知するための Subject 群
		public ReactiveProperty<long> Time  { get; } = new ReactiveProperty<long>(0L); // タイマー時間
		public ReactiveProperty<bool> IsRunning  { get; } = new ReactiveProperty<bool>(false); // 実行中か？
		public ReactiveProperty<IList<long>> Laps  { get; } = new ReactiveProperty<IList<long>>(new List<long>()); // 経過時間群
		public ReactiveProperty<bool> IsVisibleMillis  { get; } = new ReactiveProperty<bool>(true); // ミリ秒表示するか？

		// タイマーの購読状況
		private IDisposable _timerSubscription = null;

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
			Laps.Value = new List<long>();;
			IsRunning.Value = true;
			var now = DateTime.Now;

			_timerSubscription =
				Observable.Interval(TimeSpan.FromMilliseconds(10), Scheduler.Default)
					.Subscribe(time => {
						Time.Value = Convert.ToInt64((DateTime.Now - now).TotalMilliseconds);
					}); // タイマー値を通知
		}

		private void Stop() {
			if (_timerSubscription != null) {
				_timerSubscription.Dispose();
				_timerSubscription = null;
			}

			// 実行終了を通知
			IsRunning.Value = false;
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

			Laps.Value = newLaps;
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
			IsVisibleMillis.Value = !IsVisibleMillis.Value;
		}

		#region IDisposable implementation

		public void Dispose ()
		{
			Stop();
		}

		#endregion
	}

}

