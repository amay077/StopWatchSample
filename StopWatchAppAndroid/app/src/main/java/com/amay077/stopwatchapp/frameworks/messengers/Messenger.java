package com.amay077.stopwatchapp.frameworks.messengers;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.PublishSubject;
import rx.subjects.SerializedSubject;
import rx.subjects.Subject;

public class Messenger {
    private final Subject<Message, Message> _bus =
            new SerializedSubject<Message, Message >(PublishSubject.<Message>create());

    public void send(Message message) {
        _bus.onNext(message);
    }

    public <T extends Message> Observable<T> register(final Class<? extends T> messageClazz) {
        return _bus
                .ofType(messageClazz)
                .map(new Func1<Message, T>() {
                    @Override
                    public T call(Message message) {
                        return (T)message;
                    }
                });
    }
}
