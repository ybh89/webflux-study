package com.hybe.binary.core.webfluxstudy.reactivestreams.scheduler;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 비동기 처리를 해주는 Operator를 만들어보자
 */
public class SchedulerApp {
    public static void main(String[] args) {
        Publisher<Integer> publisher = new Publisher<>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        System.out.println(Thread.currentThread().getName() + " request");
                        subscriber.onNext(1);
                        subscriber.onNext(2);
                        subscriber.onNext(3);
                        subscriber.onNext(4);
                        subscriber.onNext(5);
                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };

        // publish 할 때, 별개의 스레드를 사용하는 Operator
        Publisher<Integer> pubAsyncOperator = new Publisher<Integer>() {
            ExecutorService es = Executors.newSingleThreadExecutor();

            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                es.execute(() -> publisher.subscribe(subscriber));
            }
        };

        // subscribe 할 때, 별개의 스레드를 사용하는 Operator
        Publisher<Integer> subAsyncOperator = new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                ExecutorService es = Executors.newSingleThreadExecutor(); // reactive streams 표준 사양상, 단일 스레드를 사용해야함. onNext, onError, onComplete 는 모두 단일 스레드로 동작.

                pubAsyncOperator.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        es.execute(() -> subscriber.onNext(integer));
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        es.execute(() -> subscriber.onError(throwable));
                        es.shutdown();
                    }

                    @Override
                    public void onComplete() {
                        es.execute(() -> subscriber.onComplete());
                        es.shutdown();
                    }
                });
            }
        };

        Subscriber<Integer> subscriber = new Subscriber<>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println(Thread.currentThread().getName() + " onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println(Thread.currentThread().getName() + " onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println(Thread.currentThread().getName() + " onError ");
            }

            @Override
            public void onComplete() {
                System.out.println(Thread.currentThread().getName() + " onComplete ");
            }
        };

        subAsyncOperator.subscribe(subscriber);

        System.out.println(Thread.currentThread().getName() + " exit ");
    }
}
