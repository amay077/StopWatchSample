using System;
using System.Collections.ObjectModel;
using System.Linq;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using Reactive.Bindings;
using StopWatchAppXamarinForms.Api;
using StopWatchAppXamarinForms.Api.DataModels;

namespace StopWatchAppXamarinForms.UseCases
{
    public class LocationUseCase
    {
        private readonly ILocationClient _client;

        /// <summary> 受信した緯度経度 </summary>
        public IObservable<Location> Location { get; }
        /// <summary> 実行状態 </summary>
        public IObservable<bool> IsRunning { get; }
        /// <summary> 記録された緯度経度群 </summary>
        public ObservableCollection<Location> Records { get; } = new ObservableCollection<Location>();

        /// <summary>
        /// client は Prism.Forms(というかUnity)によりDIされる
        /// </summary>
        public LocationUseCase(ILocationClient client)
        {
            _client = client;

            // LocationChanged イベントを Observable に変換
            Location = Observable.FromEventPattern<Location>(
                h => client.LocationChanged += h,
                h => client.LocationChanged -= h)
                .Select(args => args.EventArgs);
            
            // IsRunningChanged イベントを Observable に変換
            IsRunning = Observable.FromEventPattern<bool>(
                h => client.IsRunningChanged += h,
                h => client.IsRunningChanged -= h)
                .Select(args => args.EventArgs);
        }

        public void StartOrStop()
        {
            if (_client.IsRunning)
            {
                _client.Stop();
            }
            else
            {
                Records.Clear();
                _client.Start();
            }
        }

        public void Record()
        {
            if (_client.LatestLocation.HasValue)
            {
                Records.Add(_client.LatestLocation.Value);
            }
        }

        public Location? GetBestLocation()
        {
            // Records から最も精度のよい(Accuracyの小さい)Locationを返す。
            //  このメソッドは Reactive ではないので、使い方に注意が必要。
            //  Reactive にするなら Records の CollectionChanged を監視する必要あり。
            return Records
                .OrderBy(location => location.Accuracy)
                .Select(location => (Location?)location)
                .DefaultIfEmpty((Location?)null)
                .First();
        }
    }
}
