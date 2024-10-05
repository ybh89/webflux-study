package com.hybe.binary.core.webfluxstudy.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

public class MySubscriber<T> implements Subscriber<T> {
    private Subscription subscription;
    private int requestSize = 2;
    private int bufferSize = requestSize;

    @Override
    public void onSubscribe(Subscription subscription) {
        System.out.println(Thread.currentThread().getName() + " onSubscribe");
        this.subscription = subscription;
        this.subscription.request(requestSize);
    }

    @Override
    public void onNext(T t) {
        System.out.println(Thread.currentThread().getName() + " onNext - " + t);
        if (--bufferSize <= 0) {
            this.subscription.request(requestSize);
            this.bufferSize = requestSize;
        }
    }

    @Override
    public void onError(Throwable throwable) {
        System.out.println(Thread.currentThread().getName() + " onError - " + throwable.getMessage());
    }

    @Override
    public void onComplete() {
        System.out.println(Thread.currentThread().getName() + " onComplete");
    }
}
