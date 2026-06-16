package com.example.CRMTicketing.dao;

import com.example.CRMTicketing.Entity.History;
import com.example.CRMTicketing.exception.DatabaseException;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Repository;

@Repository("HistoryDao")
@RequiredArgsConstructor
public class HistoryDao {
    private final SessionFactory sessionFactory;
    public void save(History h){
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try{
            session.persist(h);
            tx.commit();
        }catch(RuntimeException e){
            tx.rollback();
            throw new DatabaseException("Error related to DB", e.getCause());
        }
    }
}
