using System;
using StopWatchApp.Core.Frameworks.Messengers;
using StopWatchApp.Core.Models;
using Reactive.Bindings;
using System.Collections.Generic;
using StopWatchApp.Core.Models;
using System.Reactive.Linq;

namespace StopWatchApp.Core.Frameworks.Messengers
{
	public class StartViewMessage : IMessage
	{
		public Type ViewModelType { get; }

		public StartViewMessage(Type viewModelType)
		{
			this.ViewModelType = viewModelType;
		}
	}

}

