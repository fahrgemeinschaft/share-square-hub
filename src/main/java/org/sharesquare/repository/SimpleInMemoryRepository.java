package org.sharesquare.repository;

import org.sharesquare.ShareSquareObject;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;

@Service
public class SimpleInMemoryRepository<T extends ShareSquareObject> implements IRepository<T> {
    HashMap<String, T> inMemData = new HashMap<>();

    @Override
    public T create(T data) {
        return inMemData.put(data.getId(), data);
    }

    @Override
    public T update(T data) {
        return inMemData.replace(data.getId(),data);
    }

    @Override
    public T delete(T data) {
        return inMemData.remove(data.getId());
    }

    @Override
    public T findById(String id) {
        return inMemData.get(id);
    }

    @Override
    public Collection<T> findMany(T searchData) {
        return inMemData.values();
    }
}
