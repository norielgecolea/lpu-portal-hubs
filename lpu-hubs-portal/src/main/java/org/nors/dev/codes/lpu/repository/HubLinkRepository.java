package org.nors.dev.codes.lpu.repository;

import java.util.List;
import java.util.Optional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.nors.dev.codes.lpu.model.HubLink;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class HubLinkRepository {

    private final SessionFactory sessionFactory;

    public HubLinkRepository(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    private Session currentSession() {
        return sessionFactory.getCurrentSession();
    }

    @Transactional(readOnly = true)
    public List<HubLink> findByPortalCode(String code, boolean enabledOnly) {
        String hql = "FROM HubLink l JOIN FETCH l.portal p WHERE p.code = :code";
        if (enabledOnly) {
            hql += " AND l.enabled = true";
        }
        hql += " ORDER BY l.sortOrder ASC, l.name ASC";
        return currentSession()
                .createQuery(hql, HubLink.class)
                .setParameter("code", code)
                .getResultList();
    }

    @Transactional(readOnly = true)
    public Optional<HubLink> findById(Long id) {
        return currentSession()
                .createQuery("FROM HubLink l JOIN FETCH l.portal WHERE l.id = :id", HubLink.class)
                .setParameter("id", id)
                .uniqueResultOptional();
    }

    @Transactional(readOnly = true)
    public boolean existsByName(String portalCode, String name, Long excludeId) {
        Long count = currentSession()
                .createQuery(
                        "SELECT COUNT(l.id) FROM HubLink l WHERE l.portal.code = :code"
                                + " AND lower(l.name) = lower(:name)"
                                + " AND (:excludeId IS NULL OR l.id <> :excludeId)",
                        Long.class
                )
                .setParameter("code", portalCode)
                .setParameter("name", name)
                .setParameter("excludeId", excludeId)
                .uniqueResult();
        return count != null && count > 0;
    }

    @Transactional
    public void persist(HubLink link) {
        Session session = currentSession();
        session.persist(link);
        session.flush();
    }

    @Transactional
    public HubLink save(HubLink link) {
        return currentSession().merge(link);
    }

    @Transactional
    public void delete(HubLink link) {
        currentSession().remove(currentSession().contains(link) ? link : currentSession().merge(link));
    }
}
