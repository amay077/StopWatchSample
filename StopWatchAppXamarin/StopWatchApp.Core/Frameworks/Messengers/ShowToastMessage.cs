using System;
using StopWatchApp.Core.Frameworks.Messengers;
using StopWatchApp.Core.Models;
using Reactive.Bindings;
using System.Collections.Generic;
using StopWatchApp.Core.Models;
using System.Reactive.Linq;

namespace StopWatchApp.Core.Frameworks.Messengers
{
	public class ShowToastMessage : IMessage
	{
		public string Text { get; }

		public ShowToastMessage (string text)
		{
			this.Text = text;
		}
	}

}

