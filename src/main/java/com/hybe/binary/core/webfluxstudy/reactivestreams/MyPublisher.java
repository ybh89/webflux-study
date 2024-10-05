package com.hybe.binary.core.webfluxstudy.reactivestreams;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MyPublisher<T> implements Publisher<T> {
    private List<T> list;
    private ExecutorService executorService;

    public MyPublisher(List<T> list, ExecutorService executorService) {
        this.list = list;
        this.executorService = executorService;
    }

    @Override
    public void subscribe(Subscriber<? super T> subscriber) {
        MySubscription<T> mySubscription = new MySubscription<>(subscriber, list.iterator(), executorService);
        subscriber.onSubscribe(mySubscription);
    }
}
