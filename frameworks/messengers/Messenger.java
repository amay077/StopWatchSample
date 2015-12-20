package com.amay077.stopwatchapp.frameworks.messengers;

import java.util.HashMap;
import java.util.Map;

import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

public class Messenger implements Subscription {
    private final Map<String, Action1<Message>> _registerMap = new HashMap<>();

    public void send(Message message) {
        final String className = message.getClass().getName();
        if (_registerMap.containsKey(className)) {
            final Action1 handler = _registerMap.get(className);
            handler.call(message);
        }
    }

    public void register(String messageClassName, Action1<Message> action) {
        _registerMap.put(messageClassName, action);
    }

    @Override
    public void unsubscribe() {
        _registerMap.clear();
    }

    @Override
    public boolean isUnsubscribed() {
        return _registerMap.size() == 0;
    }
}
