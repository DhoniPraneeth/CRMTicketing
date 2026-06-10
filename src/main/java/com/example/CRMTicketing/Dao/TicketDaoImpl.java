package com.example.CRMTicketing.Dao;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.Enums.TicketStatus;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@Transactional
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketDaoImpl implements TicketDao {

    private final SessionFactory sessionFactory;

    private Session getSession() {

        return sessionFactory
                .getCurrentSession();
    }

    @Override
    public void save(Ticket ticket) {

        getSession().persist(ticket);
    }

    @Override
    public Ticket getById(String id) {

        return getSession()
                .get(Ticket.class, id);
    }

    @Override
    public List<Ticket> getAllTickets() {

        return getSession()
                .createQuery(
                        "FROM Ticket",
                        Ticket.class)
                .list();
    }

    @Override
    public void delete(String id) {

        Ticket ticket =
                getById(id);

        if(ticket != null) {

            getSession().remove(ticket);
        }
    }

    @Override
    public void update(Ticket existing) {
        getSession().merge(existing);
    }

    @Override
    public List<Ticket> getActiveTickets() {

        Session session = sessionFactory.openSession();

        String hql = """
            FROM Ticket
            WHERE status IN
            (:open, :assigned, :progress)
            """;

        List<Ticket> tickets =
                session.createQuery(hql, Ticket.class)
                        .setParameter("open",
                                TicketStatus.OPEN)
                        .setParameter("assigned",
                                TicketStatus.ASSIGNED)
                        .setParameter("progress",
                                TicketStatus.IN_PROGRESS)
                        .getResultList();

        session.close();

        return tickets;
    }
}
