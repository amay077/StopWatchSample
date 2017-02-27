using System;
using System.Collections;
using System.Collections.Generic;
using System.Globalization;
using StopWatchAppXamarinForms.Api.DataModels;
using Xamarin.Forms;

namespace StopWatchAppXamarinForms.ValueConverters
{
    public class RecordTitleConverter : IValueConverter
    {
        public object Convert(object value, Type targetType, object parameter, CultureInfo culture)
        {
            //var records = value as ICollection<Location>;
            //if ((records?.Count ?? 0) > 0)
            var count = (int)value;
            if (count > 0)
            {
                return $"RECORD({count})";
            }
            else
            {
                return "RECORD";
            }
        }

        public object ConvertBack(object value, Type targetType, object parameter, CultureInfo culture)
        {
            throw new NotImplementedException();
        }
    }
}
