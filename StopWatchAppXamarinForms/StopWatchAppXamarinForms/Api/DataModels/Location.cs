using System;
namespace StopWatchAppXamarinForms.Api.DataModels
{
    public struct Location
    {
        public double Latitude { get; }
        public double Longitude { get; }
        public double Accuracy { get; }
        public DateTime Time { get; }

        public Location(double latitude, double longitude, double accuracy, DateTime time)
        {
            Latitude = latitude;
            Longitude = longitude;
            Accuracy = accuracy;
            Time = time;
        }
    }
}
