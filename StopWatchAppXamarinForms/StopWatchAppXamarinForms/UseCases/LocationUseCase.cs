using System;
using System.Reactive.Linq;
using System.Reactive.Subjects;
using StopWatchAppXamarinForms.Api;
using StopWatchAppXamarinForms.Api.DataModels;

namespace StopWatchAppXamarinForms.UseCases
{
    public class LocationUseCase
    {
        private readonly Subject<Location> _locationSubject = new Subject<Location>();
        private readonly ILocationClient _client;

        public IObservable<Location> Location { get; }

        public LocationUseCase(ILocationClient client)
        {
            _client = client;

            Location = Observable.FromEventPattern<Location>(
                h => client.LocationChanged += h, 
                h => client.LocationChanged -= h)
                .Select(args => args.EventArgs); 
        }

        public void Start()
        {
            _client.Start();
        }

        public void Stop()
        {
            _client.Stop();
        }
    }
}
