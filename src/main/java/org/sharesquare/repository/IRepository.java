package org.sharesquare.repository;

import org.sharesquare.ShareSquareObject;

import java.util.Collection;

public interface IRepository<T extends ShareSquareObject> {

    T create(T data);

    T update(T data);

    T delete(T data);

    T findById(String id);

    Collection<T> findMany(T searchData);
}
