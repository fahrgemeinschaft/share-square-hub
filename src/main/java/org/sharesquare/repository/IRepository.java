package org.sharesquare.repository;

import org.sharesquare.IShareSquareObject;


import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.Optional;

public interface IRepository<T extends IShareSquareObject> {

    Optional<T> create(T data);

    Optional<T> update(T data);

    Optional<T> delete(T data);

    Optional<T> findById(String id);

    Page<T> findMany(T searchData, Pageable pageable);

    Collection<T> getAll();
}
