package org.nors.dev.codes.lpu.repository;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nors.dev.codes.lpu.model.User;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class UserRepository {

    private final SessionFactory sessionFactory;

    public UserRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return currentSession()
                .createQuery("FROM User u WHERE lower(u.username) = lower(:username)", User.class)
                .setParameter("username", username)
                .uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return Optional.ofNullable(currentSession().find(User.class, id));
    }

    @Transactional(readOnly = true)
    public List<User> findAll() {
        return currentSession()
                .createQuery("FROM User u ORDER BY u.username ASC", User.class)
                .getResultList();
    }

    @Transactional
    public void persist(User user) {
        Session session = currentSession();
        session.persist(user);
        session.flush();
    }

    @Transactional
    public User save(User user) {
        return currentSession().merge(user);
    }
}
