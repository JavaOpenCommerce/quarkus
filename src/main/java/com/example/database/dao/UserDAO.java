package com.example.database.dao;

import com.example.database.entity.OrderDetails;
import com.example.database.entity.User;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

public class UserDAO implements DAO<User> {

    @Inject
    EntityManager em;

    @Override
    public Optional<User> getById(Long id) {
        User user = em.find(User.class, id);
        return Optional.ofNullable(user);
    }

    @Override
    public List<User> getAll() {
        return null;
    }

    @Override
    public void save(User user) {
        em.persist(user);
    }

    @Override
    public void update(User user) {

    }

    @Override
    public void delete(User user) {

    }

    @Override
    public void deleteById(Long id) {

    }

    public List<OrderDetails> getOrderHistoryByUserId(Long id) {
        return em.createQuery("SELECT od FROM OrderDetails od WHERE od.user.id = :id")
                .setParameter("id", id)
                .getResultList();
    }
}
