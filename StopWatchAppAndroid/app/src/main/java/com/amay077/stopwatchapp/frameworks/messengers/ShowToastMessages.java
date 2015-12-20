package com.amay077.stopwatchapp.frameworks.messengers;

/**
 * Created by hrnv on 2015/12/20.
 */
public class ShowToastMessages implements Message {
    public final String text;

    public ShowToastMessages(String text) {
        this.text = text;
    }
}
