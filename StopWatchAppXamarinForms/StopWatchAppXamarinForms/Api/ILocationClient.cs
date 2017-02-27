using System;
using StopWatchAppXamarinForms.Api.DataModels;

namespace StopWatchAppXamarinForms.Api
{
    public interface ILocationClient
    {
        event EventHandler<Location> LocationChanged;

        void Start();
        void Stop();
    }
}
