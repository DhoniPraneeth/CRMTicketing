package com.example.CRMTicketing.Dao;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.example.CRMTicketing.exception.DatabaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Repository
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketDaoImpl implements TicketDao {

    private final SessionFactory sessionFactory;

    private Session getSession() {
        return sessionFactory.getCurrentSession();
    }

    @Override
    public void save(Ticket ticket) {
        try {
            getSession().persist(ticket);
        } catch (HibernateException ex) {
            log.error("Failed to save ticket", ex);
            throw new DatabaseException("Unable to save ticket", ex);
        }
    }

    @Override
    public Ticket getById(Long id) {
        try {
            return getSession().get(Ticket.class, id);
        } catch (HibernateException ex) {
            log.error("Failed to load ticket id={}", id, ex);
            throw new DatabaseException("Unable to retrieve ticket", ex);
        }
    }

    @Override
    public List<Ticket> getAllTickets() {
        try {
            return getSession()
                    .createQuery("FROM Ticket", Ticket.class)
                    .list();
        } catch (HibernateException ex) {
            log.error("Failed to fetch all tickets", ex);
            throw new DatabaseException("Unable to fetch tickets", ex);
        }
    }

    @Override
    public void delete(Long id) {
        try {
            Ticket ticket = getById(id);
            if (ticket != null) {
                getSession().remove(ticket);
            }
        } catch (HibernateException ex) {
            log.error("Failed to delete ticket id={}", id, ex);
            throw new DatabaseException("Unable to delete ticket", ex);
        }
    }

    @Override
    public void update(Ticket existing) {
        try {
            getSession().merge(existing);
        } catch (HibernateException ex) {
            log.error("Failed to update ticket id={}", existing.getTicketId(), ex);
            throw new DatabaseException("Unable to update ticket", ex);
        }
    }

    @Override
    public List<Ticket> getActiveTickets() {
        Session session = null;
        try {
            session = sessionFactory.openSession();
            String hql = """
                FROM Ticket
                WHERE status IN
                (:open, :assigned, :progress)
                """;
            return session.createQuery(hql, Ticket.class)
                    .setParameter("open", TicketStatus.OPEN)
                    .setParameter("assigned", TicketStatus.ASSIGNED)
                    .setParameter("progress", TicketStatus.IN_PROGRESS)
                    .getResultList();
        } catch (HibernateException ex) {
            log.error("Failed to fetch active tickets", ex);
            throw new DatabaseException("Unable to fetch active tickets", ex);
        } finally {
            if (session != null) {
                session.close();
            }
        }
    }
}
