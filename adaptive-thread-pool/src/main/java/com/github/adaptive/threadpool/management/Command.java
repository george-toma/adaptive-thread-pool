package com.github.adaptive.threadpool.management;

import java.util.List;

public interface Command<T> {

    T remove();

    void remove(int val);

    List<T> shutdown();

    void clear();
}
