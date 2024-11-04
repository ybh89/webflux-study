package com.hybe.binary.core.webfluxstudy;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;

public class BackpressureTest {
    @Test
    void request() {
        // given
        Flux<Integer> publisher = Flux.range(1, 10);

        // when
        publisher
                .publishOn(Schedulers.parallel())
                .subscribe(new BaseSubscriber<Integer>() {
            @Override
            protected void hookOnSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                request(1);
            }

            @Override
            protected void hookOnNext(Integer value) {
                System.out.println("onNext: " + value);
                try {
                    Thread.sleep(500L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                request(1);
            }
        });
    }

    @Test
    void request_unbounded() {
        // given
        Flux.interval(Duration.ofMillis(100L))
                .take(100)
                .log()
                .subscribe(); // 아무것도 정의하지 않으면 기본적으로 request(unbounded) 요청
    }

    @Test
    void limitRate() throws InterruptedException {
        // given
        Flux.interval(Duration.ofMillis(1L))
                .log()
                .limitRate(1)
                .onBackpressureError()
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(2000);
    }

    @Test
    void ignore() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .doOnNext(System.out::println)
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(2000);
    }

    @Test
    void error() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .onBackpressureError()
                .doOnNext(System.out::println)
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(3000);
    }

    @Test
    void drop() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .onBackpressureDrop(dropped -> System.out.println("dropped: " + dropped))
                .doOnNext(System.out::println)
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(3000);
    }

    @Test
    void latest() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .onBackpressureLatest()
                .doOnNext(System.out::println)
                .publishOn(Schedulers.parallel())
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(3000);
    }

    @Test
    void buffer_drop_latest() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .doOnNext(data -> System.out.println("before buffer data: " + data))
                .onBackpressureBuffer(2, dropped -> System.out.println("dropped: " + dropped), BufferOverflowStrategy.DROP_LATEST)
                .doOnNext(data -> System.out.println("after buffer data: " + data))
                .publishOn(Schedulers.parallel(), false, 1)
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(3000);
    }

    @Test
    void buffer_drop_oldest() throws InterruptedException {
        // given
        // when
        Flux.interval(Duration.ofMillis(1L))
                .doOnNext(data -> System.out.println("before buffer data: " + data))
                .onBackpressureBuffer(2, dropped -> System.out.println("dropped: " + dropped), BufferOverflowStrategy.DROP_OLDEST)
                .doOnNext(data -> System.out.println("after buffer data: " + data))
                .publishOn(Schedulers.parallel(), false, 1)
                .subscribe(data -> {
                    try {
                        System.out.println("consumer: " + data);
                        Thread.sleep(5L);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                });

        Thread.sleep(3000);
    }
}
