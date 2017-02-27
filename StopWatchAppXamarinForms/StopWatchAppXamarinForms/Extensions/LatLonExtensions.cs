using System;
namespace StopWatchAppXamarinForms.Extensions
{
    public static class LatLonExtensions
    {
        public static string Format(this double self, bool isDms)
        {
            if (isDms)
            {
                double tmp = self;
                var degree = (int)Math.Floor(tmp);

                tmp = (tmp - degree) * 60;
                var minutes = (int)Math.Floor(tmp);

                var seconds = (tmp - minutes) * 60;

                return $"{degree}度{minutes}分{seconds:0.000}秒";
            }
            else
            {
                return self.ToString("0.000");
            }
        }
    }
}
