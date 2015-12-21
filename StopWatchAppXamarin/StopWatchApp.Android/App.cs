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
		public StopWatchModel StopWatch { get; } = new StopWatchModel();
		#endregion

		public IDictionary<Type, Type> VmToActivityTypes { get; } = new Dictionary<Type, Type>() 
		{
			{ typeof(MainViewModel), typeof(MainActivity) },
			{ typeof(LapViewModel), typeof(LapActivity) }
		};

		public App(IntPtr javaReference, JniHandleOwnership transfer)
			: base(javaReference, transfer)
		{
		}

		public override void OnTerminate()
		{
			StopWatch.Dispose();
			base.OnTerminate();
		}

	}
}

