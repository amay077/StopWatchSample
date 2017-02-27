using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.Reactive.Disposables;
using System.Reactive.Linq;
using Prism.Navigation;
using Prism.Services;
using Reactive.Bindings;
using Reactive.Bindings.Extensions;
using StopWatchAppXamarinForms.Api.DataModels;
using StopWatchAppXamarinForms.Extensions;
using StopWatchAppXamarinForms.Models;
using StopWatchAppXamarinForms.UseCases;

namespace StopWatchAppXamarinForms.ViewModels
{
    public class MainViewModel : IDisposable
    {
        readonly CompositeDisposable _subscriptions = new CompositeDisposable();

        // View向けに公開する変更通知プロパティ

        /// <summary> 緯度、経度、時刻 </summary>
        public ReadOnlyReactiveProperty<string> FormattedLatitude { get; }
        public ReadOnlyReactiveProperty<string> FormattedLongitude { get; }
        public ReadOnlyReactiveProperty<string> FormattedTime { get; }
        /// <summary> 実行中かどうか？ </summary>
        public ReadOnlyReactiveProperty<bool> IsRunning { get; }
        /// <summary> 度分秒表示か？ </summary>
        public ReactiveProperty<bool> IsDmsFormat { get; } = new ReactiveProperty<bool>(false);
        /// <summary> 記録した数 </summary>
        public ReadOnlyReactiveProperty<int> RecordCount { get; }

        // View向けに公開するコマンド

        /// <summary> 開始 or 終了 </summary>
        public ReactiveCommand StartOrStopCommand { get; }
        /// <summary> 緯度経度の記録 </summary>
        public ReactiveCommand RecordCommand { get; }

        public MainViewModel(INavigationService navigationService,
            IPageDialogService dialogService, LocationUseCase locationUseCase)
        {
            // ■プロパティの実装
            // LocationUseCase の各プロパティを必要なら加工して公開
            IsRunning = locationUseCase.IsRunning.ToReadOnlyReactiveProperty();

            // Location の時刻をフォーマットして公開
            FormattedTime = locationUseCase.Location
                .Select(l => l.Time.ToString("HH:mm:ss"))
                .ToReadOnlyReactiveProperty();

            // Location の緯度を度分秒または度にフォーマットして公開
            FormattedLatitude = IsDmsFormat.CombineLatest(
                locationUseCase.Location.Select(l => l.Latitude),
                    (isDms, lat) => lat.Format(isDms))
               .ToReadOnlyReactiveProperty();
            
            // Location の経度を度分秒または度にフォーマットして公開
            FormattedLongitude = IsDmsFormat.CombineLatest(
                locationUseCase.Location.Select(l => l.Longitude),
                    (isDms, lon) => lon.Format(isDms))
               .ToReadOnlyReactiveProperty();

            // 記録されたレコード群を件数として公開
            RecordCount = locationUseCase.Records
                .ToCollectionChanged()
                .Select(_ => locationUseCase.Records.Count)
                .ToReadOnlyReactiveProperty();

            //// STOP されたら、最も精度のよい位置情報を表示して、RecordsPage へ遷移
            IsRunning
                .Buffer(2, 1)
                .Where(x => x[0] && !x[1])
                .Subscribe(async _ =>
                {
                    // 最も精度のよい緯度経度を得る
                    //  返値がメソッドは、その時点の情報でしかない(Reactiveではない)ので注意すること
                    var bestLocation = locationUseCase.GetBestLocation();

                    var message = bestLocation.HasValue ?
                        $"{bestLocation.Value.Latitude.Format(IsDmsFormat.Value)}/" +
                        $"{bestLocation.Value.Longitude.Format(IsDmsFormat.Value)} です。" : 
                        "記録されてません";
                
                    //// Alert を表示させる
                    await dialogService.DisplayAlertAsync("最も精度の良い位置は", message, "Close");

                    // RecordPage へ遷移させる    
                    await navigationService.NavigateAsync("RecordsPage");
                })
                .AddTo(_subscriptions);


            // ■コマンドの実装
            // 開始 or 終了
            StartOrStopCommand = new ReactiveCommand(); // いつでも実行可能
            StartOrStopCommand.Subscribe(_ =>
                {
                    locationUseCase.StartOrStop();
                })
                .AddTo(_subscriptions);

            // 位置情報の記録
            RecordCommand = IsRunning.ToReactiveCommand(); // 実行中のみ記録可能
            RecordCommand.Subscribe(_ =>
                {
                    locationUseCase.Record();
                })
                .AddTo(_subscriptions);
        }

        #region IDisposable implementation

        public void Dispose ()
        {
            _subscriptions.Dispose();
        }

        #endregion
    }
}
