package com.skillbox.cryptobot.service;

import java.util.Collection;

public interface CRUDService<T> {
    T getByItem(T item);
    Collection<T> getAll();
    void create(T item);
    void update(T item);
    void delete(T item);
}
