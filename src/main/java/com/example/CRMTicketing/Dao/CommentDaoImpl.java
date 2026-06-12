package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.exception.DatabaseException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CommentDaoImpl implements CommentDao{

    private final SessionFactory sessionFactory;
    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    @Override
    public void save(Comment comment) {
        try {
            getSession().persist(comment);
        } catch (HibernateException ex) {
            log.error("Failed to save comment", ex);
            throw new DatabaseException("Unable to save comment", ex);
        }
    }

    @Override
    public Comment getById(Integer id) {
        try {
            return getSession().get(Comment.class,id);
        } catch (HibernateException ex) {
            log.error("Failed to load comment id={}", id, ex);
            throw new DatabaseException("Unable to retrieve comment", ex);
        }
    }

    @Override
    public List<Comment> getAllComments() {
        try {
            return getSession().createQuery("From Comment",Comment.class).list();
        } catch (HibernateException ex) {
            log.error("Failed to fetch comments", ex);
            throw new DatabaseException("Unable to fetch comments", ex);
        }
    }

    @Override
    public List<Comment> getCommentsByTicketId(Integer ticketId) {
        try {
            return getSession()
                    .createQuery("FROM Comment c WHERE c.ticketId = :ticketId", Comment.class)
                    .setParameter("ticketId", ticketId)
                    .list();
        } catch (HibernateException ex) {
            log.error("Failed to fetch comments for ticket id={}", ticketId, ex);
            throw new DatabaseException("Unable to fetch comments by ticket id", ex);
        }
    }

    @Override
    public void update(Comment comment) {
        try {
            getSession().merge(comment);
        } catch (HibernateException ex) {
            log.error("Failed to update comment id={}", comment.getCommentId(), ex);
            throw new DatabaseException("Unable to update comment", ex);
        }
    }

    @Override
    public void delete(Integer id) {
        try {
            Comment c = getSession().get(Comment.class,id);
            if(c != null) {
                getSession().remove(c);
            }
        } catch (HibernateException ex) {
            log.error("Failed to delete comment id={}", id, ex);
            throw new DatabaseException("Unable to delete comment", ex);
        }
    }
}
