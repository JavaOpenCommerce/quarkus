package com.example.database.dao;

import com.example.database.entity.OrderDetails;
import com.example.database.entity.User;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.Optional;

import static java.util.Optional.ofNullable;

public class UserDAO implements DAO<User> {

    private final EntityManager em;

    public UserDAO(EntityManager em) {
        this.em = em;
    }

    @Override
    public Optional<User> getById(Long id) {
        User savedUser = em.find(User.class, id);
        return ofNullable(savedUser);
    }

    @Override
    public List<User> getAll() {
        return em.createQuery("SELECT u FROM User u").getResultList();
    }

    @Override
    public void save(User user) {
        if (user.getId() == null) {
            em.persist(user);
        } else {
            em.merge(user);
        }
    }

    @Override
    public void delete(User user) {
        if (em.contains(user)) {
            em.remove(user);
        }
    }

    @Override
    public void deleteById(Long id) {
        em.createQuery("DELETE FROM User u WHERE u.id = :id")
                .setParameter("id", id)
                .executeUpdate();
    }

    public List<OrderDetails> getOrderHistoryByUserId(Long id) {
        return em.createQuery("SELECT od FROM OrderDetails od WHERE od.user.id = :id", OrderDetails.class)
                .setParameter("id", id)
                .getResultList();
    }
}
