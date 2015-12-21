using System;
using Android.App;
using StopWatchApp.Core.Models;
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

		public IDictionary<Type, Type> VmToActivityTypes { get; } = new Dictionary<Type, Type>();

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

