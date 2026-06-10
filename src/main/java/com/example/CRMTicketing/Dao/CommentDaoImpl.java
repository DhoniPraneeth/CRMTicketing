package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Comment;
import lombok.AllArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CommentDaoImpl implements CommentDao{

    private final SessionFactory sessionFactory;
    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    @Override
    public void save(Comment comment) {
        getSession().persist(comment);
    }

    @Override
    public Comment getById(String id) {
        return getSession().get(Comment.class,id);
    }

    @Override
    public List<Comment> getAllComments() {
        return getSession().createQuery("From Comment",Comment.class).list();
    }

    @Override
    public List<Comment> getCommentsByTicketId(String ticketId) {
        return getSession()
                .createQuery("FROM Comment c " +
                        "WHERE c.ticket.id = :ticketId",Comment.class)
                .list();
    }

    @Override
    public void update(Comment comment) {
        getSession().merge(comment);
    }

    @Override
    public void delete(String id) {
        Comment c=getSession().get(Comment.class,id);
        if(c!=null)
            getSession().remove(c);
    }
}
