package org.nors.dev.codes.lpu.repository;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nors.dev.codes.lpu.model.Portal;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class PortalRepository {

    private final SessionFactory sessionFactory;

    public PortalRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    public Optional<Portal> findByCode(String code) {
        return currentSession()
                .createQuery("FROM Portal p WHERE p.code = :code", Portal.class)
                .setParameter("code", code)
                .uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public List<Portal> findAll() {
        return currentSession()
                .createQuery("FROM Portal p ORDER BY p.code ASC", Portal.class)
                .getResultList();
    }

    @Transactional
    public Portal save(Portal portal) {
        return currentSession().merge(portal);
    }
}
