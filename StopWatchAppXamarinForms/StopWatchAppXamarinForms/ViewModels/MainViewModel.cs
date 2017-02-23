
using System;
using System.Collections.Generic;
using System.Reactive.Concurrency;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using Prism.Navigation;
using Prism.Services;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;
using StopWatchAppXamarinForms.Models;

namespace StopWatchAppXamarinForms.ViewModels
{
    public class MainViewModel : IDisposable
    {
        readonly CompositeDisposable _subscriptions = new CompositeDisposable();

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
        public ReactiveCommand StartOrStopCommand { get; }
        /// <summary> 経過時間の記録 </summary>
        public ReactiveCommand LapCommand { get; }
        /// <summary> ミリ秒以下表示の切り替え </summary>
        public ReactiveCommand ToggleVisibleMillisCommand { get; } = new ReactiveCommand(); // いつでも実行可能

        public MainViewModel(INavigationService navigationService,
            IPageDialogService dialogService, IStopWatchModel stopWatch)
        {
            // ■プロパティの実装
            // StopWatchModel の各プロパティをそのまま公開してるだけ
            IsRunning = stopWatch.IsRunning;
            FormattedLaps = stopWatch.FormattedLaps;
            IsVisibleMillis = stopWatch.IsVisibleMillis;

            // 表示用にthrottleで20ms毎に間引き。View側でやってもよいかも。
            FormattedTime = stopWatch.FormattedTime
                .Throttle(TimeSpan.FromMilliseconds(100), Scheduler.Default)
                .ToReadOnlyReactiveProperty();

            //// STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
            //IsRunning.Where(x => true)
            //    .Subscribe(async _ =>
            //    {
            //        // Alert を表示させる
            //        await dialogService.DisplayAlertAsync(
            //            "最速最遅ラップ",
            //            $"最速ラップ:{stopWatch.FormattedFastestLap.Value}, 最遅ラップ:{stopWatch.FormattedWorstLap.Value}",
            //            "閉じる");

            //        // LapActivity へ遷移させる
            //        await navigationService.NavigateAsync("LapPage");
            //    })
            //    .AddTo(_subscriptions);


            // ■コマンドの実装
            // 開始 or 終了
            StartOrStopCommand = new ReactiveCommand(); // いつでも実行可能
            StartOrStopCommand.Subscribe(_ =>
                {
                    stopWatch.StartOrStop();
                });

            // 経過時間の記録
            LapCommand = IsRunning.ToReactiveCommand(); // 実行中のみ記録可能
            LapCommand.Subscribe(_ =>
                {
                    stopWatch.Lap();
                });

            // ミリ秒以下表示の切り替え
            ToggleVisibleMillisCommand = new ReactiveCommand(); // いつでも実行可能
            ToggleVisibleMillisCommand.Subscribe(_ =>
                {
                    stopWatch.ToggleVisibleMillis();
                });
        }

        #region IDisposable implementation

        public void Dispose ()
        {
            _subscriptions.Dispose();
        }

        #endregion
    }
}
