package com.amay077.stopwatchapp.frameworks;

import rx.Observable;

/**
 * ViewModel のコマンドを示すインターフェース
 */
public interface Command {

    /** このコマンドが実行可能かを示すフラグの更新を通知するObservable */
    Observable<Boolean> canExecuteObservable();

    /** このコマンドの処理を実装する */
    void execute(); // パラメータ付きコマンドは未実装
}
