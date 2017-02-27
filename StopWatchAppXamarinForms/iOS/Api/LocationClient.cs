using System;
using System.Linq;
using CoreLocation;
using StopWatchAppXamarinForms.Api;
using StopWatchAppXamarinForms.Api.DataModels;
using Xamarin.Forms.Platform.iOS;

namespace StopWatchAppXamarinForms.iOS.Api
{
    public class LocationClient : ILocationClient
    {
        readonly CLLocationManager _locationManager;

        public Location? LatestLocation { get; private set; }
        public bool IsRunning { get; private set; }

        public event EventHandler<Location> LocationChanged;
        public event EventHandler<bool> IsRunningChanged;

        public LocationClient()
        {
            _locationManager= new CLLocationManager();

            _locationManager.LocationsUpdated += (sender, e) =>
            {
                var l = e?.Locations?.FirstOrDefault() ?? null;
                if (l != null)
                {
                    var location = new Location(
                        l.Coordinate.Latitude,
                        l.Coordinate.Longitude,
                        l.HorizontalAccuracy,
                        l.Timestamp.ToDateTime()
                    );
                    LatestLocation = location;
                    LocationChanged?.Invoke(this, location);
                }
            };

            _locationManager.DistanceFilter = 100;
            _locationManager?.RequestAlwaysAuthorization();
        }

        public void Start()
        {
            _locationManager.StartUpdatingLocation();
            IsRunning = true;
            IsRunningChanged?.Invoke(this, IsRunning);
        }

        public void Stop()
        {
            _locationManager.StopUpdatingLocation();
            IsRunning = false;
            IsRunningChanged?.Invoke(this, IsRunning);
        }
    }
}
