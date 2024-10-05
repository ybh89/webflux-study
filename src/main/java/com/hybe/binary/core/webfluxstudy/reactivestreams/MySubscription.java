package com.hybe.binary.core.webfluxstudy.reactivestreams;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class MySubscription<T> implements Subscription {
    private Subscriber<? super T> subscriber;
    private Iterator<T> iterator;
    private ExecutorService executorService;

    public MySubscription(Subscriber<? super T> subscriber, Iterator<T> iterator, ExecutorService executorService) {
        this.subscriber = subscriber;
        this.iterator = iterator;
        this.executorService = executorService;
    }

    /**
     * reactive streams 는 push 방식이라면서 웬 요청(request)?
     * 요청이라고해서 데이터를 가져오는 poll 방식이 아니다.
     * 메소드 시그니처를 보면 리턴타입이 void 인걸 확인할 수 있다.
     * 이 request 라는 요청은 요청즉시 데이터를 가져오는것이 아니라, 몇 개의 데이터를 보내줘~ 라는 백프레셔의 역할을 한다.
     */
    @Override
    public void request(long l) {
        // future 로 cancel 적용
        Future<?> future = executorService.submit(() -> {
            int i = 0;
            while (i++ < l) { // backpressure
                if (!iterator.hasNext()) {
                    subscriber.onComplete();
                    break;
                }
                subscriber.onNext(iterator.next());
            }
        });
    }

    @Override
    public void cancel() {

    }
}
