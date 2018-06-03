package com.github.sliding.adaptive.thread.pool.management;

import java.util.List;

public interface Command<T> {

    T remove();

    void remove(int val);

    List<T> shutdown();

    void clear();

    void add(T... t);

}
