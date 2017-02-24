using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.Linq;
using System.Reactive.Concurrency;
using System.Reactive.Linq;
using System.Threading;
using Reactive.Bindings;

namespace StopWatchAppXamarinForms.Models
{
    /// <summary>
    /// ストップウォッチの機能を実装したロジッククラス
    /// </summary>
    public class StopWatchModel : IStopWatchModel
    {
        // ストップウォッチの状態を更新＆通知するための ReactiveProperty 群(要は Subject)
        private readonly ReactiveProperty<long> _time = new ReactiveProperty<long>(Scheduler.Default, 0L); // タイマー時間

        private readonly ReactiveProperty<bool> _isRunning = new ReactiveProperty<bool>(Scheduler.Default, false); // 実行中か？

        private readonly ReactiveProperty<IList<long>> _laps = new ReactiveProperty<IList<long>>(new List<long>()); // 経過時間群

        private readonly ReactiveProperty<bool> _isVisibleMillis = new ReactiveProperty<bool>(Scheduler.Default, true); // ミリ秒表示するか？
        private readonly ReactiveProperty<string> _timeFormat;

        // Model として公開するプロパティ
        public ReadOnlyReactiveProperty<string> FormattedTime { get; } // フォーマットされたタイマー時間

        public ReadOnlyReactiveProperty<bool> IsRunning { get; } // 実行中か？
        public ReadOnlyReactiveProperty<IEnumerable<string>> FormattedLaps { get; } // フォーマットされた経過時間群
        public ReadOnlyReactiveProperty<bool> IsVisibleMillis { get; } // ミリ秒表示するか？
        public ReadOnlyReactiveProperty<string> FormattedFastestLap { get; }
        public ReadOnlyReactiveProperty<string> FormattedWorstLap { get; }

        // タイマーの購読状況
        private IDisposable _timerSubscription = null;

        private long _startTime;

        public StopWatchModel()
        {
            Debug.WriteLine("StopWatchModel ctor");

            IsRunning = _isRunning.ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);
            IsVisibleMillis = _isVisibleMillis.ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);

            _timeFormat = IsVisibleMillis
                .Select(v => v ? @"mm\:ss\.fff" : @"mm\:ss")
                .ToReactiveProperty(raiseEventScheduler: Scheduler.Default);

            FormattedTime = _time.CombineLatest(_timeFormat, (time, format) =>
                    TimeSpan.FromMilliseconds(time).ToString(format))
                .ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);

            FormattedLaps = _laps.CombineLatest(_timeFormat,
                    (laps, f) => laps.Select((x, i) => TimeSpan.FromMilliseconds(x).ToString(f)))
                .ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);

            FormattedFastestLap = _laps.CombineLatest(_timeFormat, (laps, format) =>
                {
                var fastest = laps.Count > 0 ? laps.Min() : 0;
                    return TimeSpan.FromMilliseconds(fastest).ToString(format);
                })
                .ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);

            FormattedWorstLap = _laps.CombineLatest(_timeFormat, (laps, format) =>
                {
                    var worst = laps.Count > 0 ? laps.Max() : 0;
                    return TimeSpan.FromMilliseconds(worst).ToString(format);
                })
                .ToReadOnlyReactiveProperty(eventScheduler: Scheduler.Default);
        }

        /// <summary>
        /// 計測を開始または終了する(time プロパティの更新を開始する)
        /// </summary>
        public void StartOrStop()
        {
            if (!_isRunning.Value)
            {
                Start();
            }
            else
            {
                Stop();
            }
        }

        private void Start()
        {
            if (_isRunning.Value)
            {
                Stop();
            }

            // 開始時に Laps をクリア、実行中フラグをON
            // RxJava の compose がないので「購読が開始された時」でないのが微妙…
            _laps.Value = new List<long>();
            ;
            _isRunning.Value = true;
            _startTime = DateTime.Now.Ticks;

            _timerSubscription =
                Observable.Interval(TimeSpan.FromMilliseconds(100))
                          .Subscribe(_ => UpdateTime()); // タイマー値を通知
        }

        private void Stop()
        {
            if (_timerSubscription != null)
            {
                _timerSubscription.Dispose();
                _timerSubscription = null;
            }

            UpdateTime();

            // 実行終了を通知
            _isRunning.Value = false;
        }

        void UpdateTime()
        {
            _time.Value = (DateTime.Now.Ticks - _startTime) / 10000;
        }

        /// <summary>
        /// 経過時間を記録する(経過時間を laps プロパティに追加する)
        /// </summary>
        public void Lap()
        {
            var laps = _laps.Value;
            var newLaps = new List<long>();

            newLaps.AddRange(laps);

            var totalLap = 0L;
            foreach (var lap in laps)
            {
                totalLap += lap;
            }

            UpdateTime();
            newLaps.Add(_time.Value - totalLap);

            _laps.Value = newLaps;
        }

        /// <summary>
        /// 小数点以下の表示有無を切り替える（isVisibleMillis プロパティを toggle する）
        /// </summary>
        public void ToggleVisibleMillis()
        {
            _isVisibleMillis.Value = !_isVisibleMillis.Value;
        }

        #region IDisposable implementation

        public void Dispose()
        {
            Stop();
        }

        #endregion
    }
}