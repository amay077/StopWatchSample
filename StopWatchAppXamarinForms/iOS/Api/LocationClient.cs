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

        public event EventHandler<Location> LocationChanged;

        public LocationClient()
        {
            _locationManager= new CLLocationManager();

            _locationManager.LocationsUpdated += (sender, e) =>
            {
                var l = e?.Locations?.FirstOrDefault() ?? null;
                if (l != null)
                {
                    LocationChanged?.Invoke(this, new Location(
                        l.Coordinate.Latitude,
                        l.Coordinate.Longitude,
                        l.HorizontalAccuracy,
                        l.Timestamp.ToDateTime()
                    ));
                }
            };

            _locationManager.DistanceFilter = 100;
            _locationManager?.RequestAlwaysAuthorization();
        }

        public void Start()
        {
            _locationManager.StartUpdatingLocation();
        }

        public void Stop()
        {
            _locationManager.StopUpdatingLocation();
        }
    }
}
