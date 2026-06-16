package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.dao.Dao;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentService {
    private final TicketService ticketService;
    private final Dao dao;
    public boolean postComment(Comment comment,Long id) {
        comment.setTicket_id(id);
        dao.saveOrUpdate(comment);
        return true;
    }
    public List<Comment> get(Long id) {
        String hql="From Comment where ticket_id = "+id;
        return dao.getByQuery(hql);
    }
}
