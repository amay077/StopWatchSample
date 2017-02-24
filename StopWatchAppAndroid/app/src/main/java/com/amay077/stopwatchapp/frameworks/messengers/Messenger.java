package com.amay077.stopwatchapp.frameworks.messengers;

import io.reactivex.Observable;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class Messenger {
    private final Subject<Message> _bus = PublishSubject.<Message>create().toSerialized();

    public void send(Message message) {
        _bus.onNext(message);
    }

    public <T extends Message> Observable<T> register(final Class<? extends T> messageClazz) {
        Subject<Object> objectSubject = PublishSubject.create().toSerialized();
        return _bus
                .ofType(messageClazz)
                .map(message -> {
                    return (T)message;
                });
    }
}
