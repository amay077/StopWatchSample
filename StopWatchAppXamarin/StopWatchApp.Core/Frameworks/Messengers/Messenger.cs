using System;
using System.Collections.Generic;

namespace StopWatchApp.Core.Frameworks.Messengers
{
	public class Messenger : IDisposable
	{
		private readonly IDictionary<string, Action<IMessage>> _registerMap = new Dictionary<string, Action<IMessage>>();

		public void Send(IMessage message)
		{
			var className = message.GetType().Name;
			if (_registerMap.ContainsKey(className)) {
				var handler = _registerMap[className];
				handler(message);
			}
		}

		public void Register(string messageClassName, Action<IMessage> action)
		{
			_registerMap.Add(messageClassName, action);
		}

		#region IDisposable implementation

		public void Dispose()
		{
			_registerMap.Clear();
		}

		#endregion
	}

}

