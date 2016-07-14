using System;

using Reactive.Bindings;
using System.Collections.Generic;
using System.Reactive.Linq;
using System.Linq;

namespace StopWatchApp.Core.Extensions
{
    public static class FormattedLapsExtensions
    {
        public static ReadOnlyReactiveProperty<IEnumerable<string>> ToNumberedLaps(
            this ReadOnlyReactiveProperty<IEnumerable<string>> self)
        {
            return self.Select(laps => laps.Select((x, i) => $"{i + 1}.  {x}"))
                .ToReadOnlyReactiveProperty();

        }
    }
}


