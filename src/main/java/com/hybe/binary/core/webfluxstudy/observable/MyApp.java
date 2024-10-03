package com.hybe.binary.core.webfluxstudy.observable;

import com.hybe.binary.core.webfluxstudy.observable.iterable.MyIterable;

public class MyApp {
    /**
     * Iterable 과 Observable 의 기능과 결과는 동일하지만 Pull 방식, Push 방식의 차이가 있음.
     * Observable 에서 사용되는 개념이 reactive streams 의 기반이 됨.
     */
    public static void main(String[] args) {
        Integer[] array = new Integer[]{1,2,3,4,5};
        doIterable(array);
        doObservable(array);
    }

    private static void doObservable(Integer[] array) {
        MyObservable myObservable = new MyObservable(array);
        MyObserver myObserver = new MyObserver();
        myObservable.addObserver(myObserver); // 구독
        myObservable.run();
    }

    private static void doIterable(Integer[] array) {
        MyIterable<Integer> myIterable = new MyIterable<>(array);

        for (Integer data : myIterable) {
            System.out.println(data);
        }
    }
}
