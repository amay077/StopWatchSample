# StopWatchSample

[JXUGC #9 Xamarin.Forms Mvvm 実装方法 Teachathon を開催しました - Xamarin 日本語情報](http://ytabuchi.hatenablog.com/entry/2015/12/20/012007)

への My Answer です。

Android-Java版も作ってみました。

Java版は RxJava, Xamarin版は Reactive Extensions と ReactiveProperty を使っています。
それもあってか、 [@okazuki さんの](https://github.com/runceel/JXUG) に近い感じになったかも。

## プロジェクト構成

1. Android-Java版 - [StopWatchAppAndroid](https://github.com/amay077/StopWatchSample/tree/master/StopWatchAppAndroid)
1. Xamarin.Android版 - [StopWatchAppXamarin/StopWatchApp.Android](https://github.com/amay077/StopWatchSample/tree/master/StopWatchAppXamarin/StopWatchApp.Android)
1. Xamarin.iOS版 - [StopWatchAppXamarin/StopWatchApp.iOS](https://github.com/amay077/StopWatchSample/tree/master/StopWatchAppXamarin/StopWatchApp.iOS)

Xamarin.Formsは使ってませんが、Android/iOSで Model,ViewModelをPCLで共通化([StopWatchApp.Core](https://github.com/amay077/StopWatchSample/tree/master/StopWatchAppXamarin/StopWatchApp.Core))しています

[Issues](https://github.com/amay077/StopWatchSample/issues) でのツッコミ、お待ちしております。
