using System;
using Prism.Mvvm;
using Prism.Unity;
using StopWatchAppXamarinForms.Views;
using Xamarin.Forms;

namespace StopWatchAppXamarinForms
{
    public partial class App : PrismApplication
    {
        protected override void OnInitialized()
        {
            NavigationService.NavigateAsync("NavigationPage/MainPage");
        }

        protected override void RegisterTypes()
        {
            Container.RegisterTypeForNavigation<MainPage>();
            Container.RegisterTypeForNavigation<LapPage>();
        }

        protected override void ConfigureViewModelLocator()
        {
            base.ConfigureViewModelLocator();
            ViewModelLocationProvider.SetDefaultViewTypeToViewModelTypeResolver(viewType =>
            {
                var vmNameSpace = viewType.Namespace.Replace("Views", "ViewModels");
                var vmClassName = viewType.Name.Replace("Page", "ViewModel");
                var vmTypeName = $"{vmNameSpace}.{vmClassName}";
                return Type.GetType(vmTypeName);
            });
        }
    }
}
