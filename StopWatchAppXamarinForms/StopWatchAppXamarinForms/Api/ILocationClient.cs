﻿using System;
using StopWatchAppXamarinForms.Api.DataModels;

namespace StopWatchAppXamarinForms.Api
{
    public interface ILocationClient
    {
        event EventHandler<Location> LocationChanged;
        event EventHandler<bool> IsRunningChanged;

        bool IsRunning { get; }

        void Start();
        void Stop();
    }
}