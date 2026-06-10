package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Ticket;
import java.util.List;

public interface TicketDao {

    void save(Ticket ticket);

    Ticket getById(String id);

    List<Ticket> getAllTickets();

    void delete(String id);

    void update(Ticket existing);
    List<Ticket> getActiveTickets();
}