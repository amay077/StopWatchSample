using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using Prism.Navigation;
using Prism.Services;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;
using StopWatchAppXamarinForms.Extensions;
using StopWatchAppXamarinForms.Models;
using StopWatchAppXamarinForms.UseCases;

namespace StopWatchAppXamarinForms.ViewModels
{
    public class MainViewModel : IDisposable
    {
        readonly CompositeDisposable _subscriptions = new CompositeDisposable();

        // ■ViewModel として公開するプロパティ

        /// <summary> 緯度、経度、時刻 </summary>
        public ReadOnlyReactiveProperty<string> FormattedLatitude { get; }
        public ReadOnlyReactiveProperty<string> FormattedLongitude { get; }
        public ReadOnlyReactiveProperty<string> FormattedTime { get; }
        /// <summary> 実行中かどうか？ </summary>
        public ReadOnlyReactiveProperty<bool> IsRunning { get; }
        /// <summary> 度分秒表示か？ </summary>
        public ReactiveProperty<bool> IsDmsFormat { get; } = new ReactiveProperty<bool>(false);

        // ■ViewModel として公開するコマンド

        /// <summary> 開始 or 終了 </summary>
        public ReactiveCommand StartOrStopCommand { get; }
        /// <summary> 緯度経度の記録 </summary>
        public ReactiveCommand RecordCommand { get; }

        public MainViewModel(INavigationService navigationService,
            IPageDialogService dialogService, LocationUseCase locationUseCase)
        {
            // ■プロパティの実装
            // StopWatchModel の各プロパティをそのまま公開してるだけ
            IsRunning = locationUseCase.IsRunning.ToReadOnlyReactiveProperty();

            // 表示用にthrottleで20ms毎に間引き。View側でやってもよいかも。
            //FormattedTime = stopWatch.FormattedTime
            //    //.Do(x=> Debug.WriteLine($"Throttled:{x}"))
            //    .ToReadOnlyReactiveProperty();
            FormattedTime = locationUseCase.Location
                .Select(l => l.Time.ToString("HH:mm:ss"))
                .ToReadOnlyReactiveProperty();

            FormattedLatitude = IsDmsFormat.CombineLatest(
                locationUseCase.Location.Select(l => l.Latitude),
                    (isDms, lat) => lat.Format(isDms))
               .ToReadOnlyReactiveProperty();
            
            FormattedLongitude = IsDmsFormat.CombineLatest(
                locationUseCase.Location.Select(l => l.Longitude),
                    (isDms, lon) => lon.Format(isDms))
               .ToReadOnlyReactiveProperty();

            //// STOP されたら、最速／最遅ラップを表示して、LapActivity へ遷移
            IsRunning.Buffer(2, 1).Where(x => x[0] && !x[1])
                .Subscribe(async _ =>
                {
                    //// Alert を表示させる
                    //await dialogService.DisplayAlertAsync(
                    //    "Fastest/Worst Lap",
                    //    $"Fastest:{stopWatch.FormattedFastestLap.Value}\n" +
                    //    $"Worst:{stopWatch.FormattedWorstLap.Value}",
                    //    "Close");

                    // RecordPage へ遷移させる
                    await navigationService.NavigateAsync("RecordPage");
                })
                .AddTo(_subscriptions);


            // ■コマンドの実装
            // 開始 or 終了
            StartOrStopCommand = new ReactiveCommand(); // いつでも実行可能
            StartOrStopCommand.Subscribe(_ =>
                {
                    locationUseCase.StartOrStop();
                });

            // 位置情報の記録
            RecordCommand = IsRunning.ToReactiveCommand(); // 実行中のみ記録可能
            RecordCommand.Subscribe(_ =>
                {
                    locationUseCase.Record();
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
