using System;
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

        public IObservable<Location> Location { get; }
        public IObservable<bool> IsRunning { get; }

        public LocationUseCase(ILocationClient client)
        {
            _client = client;

            Location = Observable.FromEventPattern<Location>(
                h => client.LocationChanged += h,
                h => client.LocationChanged -= h)
                .Select(args => args.EventArgs);
            
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
                _client.Start();
            }
        }

        public void Record()
        {
            throw new NotImplementedException();
        }
    }
}
