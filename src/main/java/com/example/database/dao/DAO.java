package com.example.database.dao;

import java.util.List;
import java.util.Optional;

public interface DAO<T> {

    Optional<T> getById(Long id);
    List<T> getAll();
    void save(T t);
    void update(T t);
    void delete(T t);
    void deleteById(Long id);
}
