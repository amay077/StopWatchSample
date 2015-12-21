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

namespace StopWatchApp.iOS.Views
{
	[Register ("LapViewController")]
	partial class LapViewController
	{
		[Outlet]
		[GeneratedCode ("iOS Designer", "1.0")]
		UITableView tableLaps { get; set; }

		void ReleaseDesignerOutlets ()
		{
			if (tableLaps != null) {
				tableLaps.Dispose ();
				tableLaps = null;
			}
		}
	}
}
