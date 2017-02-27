using System;
using System.Reactive.Subjects;
using Android.Content;
using Android.Locations;
using Android.OS;
using StopWatchAppXamarinForms.Api;
using StopWatchAppXamarinForms.Api.DataModels;
using ALocation = Android.Locations.Location;
using Location = StopWatchAppXamarinForms.Api.DataModels.Location;

namespace StopWatchAppXamarinForms.Droid.Api
{
    public class LocationClient : Java.Lang.Object, ILocationClient, ILocationListener
    {
        private static readonly DateTime UNIX_EPOCH = new DateTime(1970, 1, 1, 0, 0, 0, DateTimeKind.Utc);
        private readonly LocationManager _locationManager;

        public event EventHandler<Location> LocationChanged;
        public event EventHandler<bool> IsRunningChanged;

        public bool IsRunning { get; private set; }

        public LocationClient(Context context)
        {
            _locationManager = (LocationManager)context.GetSystemService(Context.LocationService);
        }

        public void Start()
        {
            IsRunning = true;
            IsRunningChanged?.Invoke(this, true);
            _locationManager.RequestLocationUpdates(LocationManager.GpsProvider, 0, 0, this);
        }

        public void Stop()
        {
            _locationManager.RemoveUpdates(this);
            IsRunning = false;
            IsRunningChanged?.Invoke(this, false);
        }

        void ILocationListener.OnLocationChanged(ALocation location)
        {
            LocationChanged?.Invoke(this, new Location(
                location.Latitude,
                location.Longitude,
                location.Accuracy,
                UNIX_EPOCH.AddMilliseconds(location.Time)
            ));
        }

        void ILocationListener.OnProviderDisabled(string provider)
        {
        }

        void ILocationListener.OnProviderEnabled(string provider)
        {
        }

        void ILocationListener.OnStatusChanged(string provider, Availability status, Bundle extras)
        {
        }
    }
}
