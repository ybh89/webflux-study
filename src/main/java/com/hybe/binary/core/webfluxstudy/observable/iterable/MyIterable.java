package com.hybe.binary.core.webfluxstudy.observable.iterable;

import java.util.Iterator;

public class MyIterable<T> implements Iterable<T> {
    private final MyIterator<T> myIterator;

    public MyIterable(T[] array) {
        this.myIterator = new MyIterator<>(array);
    }

    @Override
    public Iterator<T> iterator() {
        return myIterator;
    }
}
