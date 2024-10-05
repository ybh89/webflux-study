package com.hybe.binary.core.webfluxstudy.reactivestreams;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MyApp {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();

        List<Integer> list = List.of(1,2,3,4,5,6,7,8,9,10);
        MyPublisher<Integer> publisher = new MyPublisher<>(list, executorService);
        MySubscriber<Integer> subscriber = new MySubscriber<>();
        publisher.subscribe(subscriber);

        executorService.awaitTermination(10, TimeUnit.HOURS);
        executorService.shutdown();
    }
}
