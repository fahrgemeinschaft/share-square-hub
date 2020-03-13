package org.sharesquare.repository;

import org.sharesquare.IShareSquareObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SimpleInMemoryRepository<T extends IShareSquareObject> implements IRepository<T> {
    HashMap<String, T> inMemData = new HashMap<>();

    @Override
    public Optional<T> create(T data) {
        return Optional.ofNullable(inMemData.put(data.getId().toString(), data));
    }

    @Override
    public Optional<T> update(T data) {
        return Optional.ofNullable(inMemData.replace(data.getId().toString(),data));
    }

    @Override
    public Optional<T> delete(T data) {
        return Optional.ofNullable(inMemData.remove(data.getId()));
    }

    @Override
    public Optional<T> findById(String id) {
        return Optional.ofNullable(inMemData.get(id));
    }

    @Override
    public Page<T> findMany(T searchData, Pageable page) {
        List<T> l = inMemData.values().stream().collect(Collectors.toList());
        PageImpl<T> result = new PageImpl<T>(l ,page, l.size());
        return result;
    }

    @Override
    public Collection<T> getAll() {
        return inMemData.values();
    }
}
