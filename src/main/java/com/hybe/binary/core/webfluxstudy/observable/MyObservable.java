package com.hybe.binary.core.webfluxstudy.observable;

import java.util.Observable;

public class MyObservable extends Observable implements Runnable {
    private Integer[] array;

    public MyObservable(Integer[] array) {
        this.array = array;
    }

    @Override
    public void run() {
        for (Integer integer : array) {
            setChanged();
            notifyObservers(integer);
            // Push -> 데이터를 구독자에게 밀어넣어줌,
            // void method(data) -> 리턴타입 없고, 파라미터만 있음.
        }
    }
}
