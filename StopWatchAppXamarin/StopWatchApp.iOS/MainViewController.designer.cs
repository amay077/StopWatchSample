// WARNING
//
// This file has been generated automatically by Xamarin Studio from the outlets and
// actions declared in your storyboard file.
// Manual changes to this file will not be maintained.
//
using Foundation;
using System;
using System.CodeDom.Compiler;
using UIKit;

namespace StopWatchApp.iOS
{
	[Register ("MainViewController")]
	partial class MainViewController
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIButton buttonLap { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UIButton buttonStartStop { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UILabel labelTime { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UISwitch switchVisibleMillis { get; set; }

		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UITableView tableLaps { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (buttonLap != null) {
				buttonLap.Dispose ();
				buttonLap = null;
			}
			if (buttonStartStop != null) {
				buttonStartStop.Dispose ();
				buttonStartStop = null;
			}
			if (labelTime != null) {
				labelTime.Dispose ();
				labelTime = null;
			}
			if (switchVisibleMillis != null) {
				switchVisibleMillis.Dispose ();
				switchVisibleMillis = null;
			}
			if (tableLaps != null) {
				tableLaps.Dispose ();
				tableLaps = null;
			}
		}
	}
}
