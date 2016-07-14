using System;
using Android.App;
using StopWatchApp.Android.Views;
using StopWatchApp.Core.Models;
using StopWatchApp.Core.ViewModels;
using System.Collections.Generic;
using Android.Runtime;

namespace StopWatchApp.Android
{
	[Application]
	public class App : Application, IModelPool
	{
		#region IModelPool implementation
        public StopWatchModel StopWatch { get; private set; }
		#endregion

		public IDictionary<Type, Type> VmToActivityTypes { get; } = new Dictionary<Type, Type>() 
		{
			{ typeof(MainViewModel), typeof(MainActivity) },
			{ typeof(LapViewModel), typeof(LapActivity) }
		};

        public App(IntPtr handle, JniHandleOwnership transfer) : base(handle, transfer)
        {
        }

        public override void OnCreate()
        {
            base.OnCreate();
            this.StopWatch = new StopWatchModel();
        }

		public override void OnTerminate()
		{
			StopWatch.Dispose();
			base.OnTerminate();
		}

	}
}

