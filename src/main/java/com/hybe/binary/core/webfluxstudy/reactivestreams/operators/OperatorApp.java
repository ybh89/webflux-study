package com.hybe.binary.core.webfluxstudy.reactivestreams.operators;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.List;
import java.util.function.Function;

/**
 * Publisher -> data1 -> Operator1 -> data2 -> Operator2 -> data3 -> Subscriber
 * 중간에 Operator 가 데이터를 변환하는 동작 원리.
 * Reactor 는 Operator 를 이런식으로 제네릭하게 구현해서 제공.
 */
public class OperatorApp {
    public static void main(String[] args) {
        // map operator
        List<Integer> list = List.of(1,2,3,4,5,6,7,8,9,10);
        Publisher<Integer> publisher = getPublisher(list);
        Publisher<Integer> mapOperator1 = getMapOperator(publisher, (a) -> a * 10);
        Publisher<Integer> mapOperator2 = getMapOperator(mapOperator1, (a) -> a + 1);//mapOperator1 을 publisher 로 넘겨줌. Operator 도 Publisher 임.
        Subscriber<Integer> subscriber = getSubscriber();
        mapOperator2.subscribe(subscriber);

        System.out.println("====================");

        // sum operator
        Publisher<Integer> publisher2 = getPublisher(list);
        Publisher<Integer> sumOperator = getSumOperator(publisher2);
        Subscriber<Integer> subscriber2 = getSubscriber();
        sumOperator.subscribe(subscriber2);
    }

    private static Publisher<Integer> getSumOperator(Publisher<Integer> publisher) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                publisher.subscribe(new Subscriber<Integer>() {
                    int sum = 0;
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);
                    }

                    @Override
                    public void onNext(Integer integer) {
                        sum += integer;
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        subscriber.onError(throwable);
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onNext(sum);
                        subscriber.onComplete();
                    }
                });
            }
        };
    }

    private static Publisher<Integer> getMapOperator(Publisher<Integer> publisher, Function<Integer, Integer> function) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                publisher.subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscriber.onSubscribe(subscription);// Operator 에서 새로 만든 이 구독자는 그냥 다운 스트림의 구독자에게 전달만 해줌
                    }

                    @Override
                    public void onNext(Integer integer) {
                        subscriber.onNext(function.apply(integer));// 여기서 데이터값만 map(변환)해서 다운 스트림의 구독자에게 전달
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        subscriber.onError(throwable);// Operator 에서 새로 만든 이 구독자는 그냥 다운 스트림의 구독자에게 전달만 해줌
                    }

                    @Override
                    public void onComplete() {
                        subscriber.onComplete();// Operator 에서 새로 만든 이 구독자는 그냥 다운 스트림의 구독자에게 전달만 해줌
                    }
                });
            }
        };
    }

    private static Subscriber<Integer> getSubscriber() {
        return new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription subscription) {
                System.out.println("onSubscribe");
                subscription.request(Long.MAX_VALUE);
            }

            @Override
            public void onNext(Integer integer) {
                System.out.println("onNext " + integer);
            }

            @Override
            public void onError(Throwable throwable) {
                System.out.println("onError " + throwable.getMessage());
            }

            @Override
            public void onComplete() {
                System.out.println("onComplete");
            }
        };
    }

    private static Publisher<Integer> getPublisher(List<Integer> list) {
        return new Publisher<Integer>() {
            @Override
            public void subscribe(Subscriber<? super Integer> subscriber) {
                subscriber.onSubscribe(new Subscription() {
                    @Override
                    public void request(long l) {
                        list.forEach(integer -> subscriber.onNext(integer));
                        subscriber.onComplete();
                    }

                    @Override
                    public void cancel() {

                    }
                });
            }
        };
    }
}
