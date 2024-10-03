package com.hybe.binary.core.webfluxstudy.observable.iterable;

import java.util.Iterator;

public class MyIterator<T> implements Iterator<T> {
    private T[] array;
    private int index = 0;

    public MyIterator(T[] array) {
        this.array = array;
    }

    @Override
    public boolean hasNext() {
        return index < array.length;
    }

    /**
     * pull 방식.
     * T next() -> 직접 데이터를 가져옴. 리턴타입 있고, 파라미터 없음.
     */
    @Override
    public T next() {
        return array[index++];
    }
}
