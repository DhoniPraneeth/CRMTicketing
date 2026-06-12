package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.History;
import lombok.RequiredArgsConstructor;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

@Repository("HistoryDao")
@RequiredArgsConstructor
public class HistoryDao {
    private final SessionFactory sessionFactory;
    public Session getSession(){
        return sessionFactory.getCurrentSession();
    }
    public void save(History h){
        getSession().persist(h);
    }
}
