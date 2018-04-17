package com.github.sliding.adaptive.thread.pool.report.repository;

public interface RepositoryState<K, V> {
    V get(K key);

    void put(K key, V value);

    void clear();

    void close();

    boolean isClosed();

    String name();

    void remove(K key);
}
