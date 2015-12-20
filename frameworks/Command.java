package com.amay077.stopwatchapp.frameworks;

import rx.Observable;

/**
 * Created by hrnv on 2015/12/20.
 */
public interface Command {
    Observable<Boolean> canExecuteObservable();
    void execute(); // パラメータ付きコマンドは未実装
}
