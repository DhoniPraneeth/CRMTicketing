package com.example.CRMTicketing.dao;

import com.example.CRMTicketing.Entity.Comment;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("Dao")
@Slf4j
@RequiredArgsConstructor
public class DaoImpl<T> implements Dao<T>{

    private final SessionFactory sessionFactory;

    @Override
    public void saveOrUpdate(Object obj) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try {
            session.persist(obj);
            tx.commit();
        }catch (Exception e){
            tx.rollback();
            log.debug(e.getMessage());
        }
    }

    @Override
    public T getById(Class<T> type, Long id) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try{
            T object=session.get(type,id);
            tx.commit();
            return object;
        } catch (RuntimeException e) {
            tx.rollback();
            throw new RuntimeException(e);
        }finally {
            session.close();
        }
    }


    @Override
    public List<T> get(Class<T> type,Integer limit, Integer offset) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        String hql="From "+type.getSimpleName();
        try{
            List<T> l=session
                    .createQuery(hql,type)
                    .setMaxResults(limit)
                    .setFirstResult(offset)
                    .getResultList();
            tx.commit();
            return l;
        }catch(RuntimeException ex){
            tx.rollback();
            throw new RuntimeException(ex);
        }finally{
            session.close();
        }
    }

    @Override
    public boolean deleteById(Long id,Class<T> type) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try{
            Object ob=session.get(type,id);
            if(ob==null){
                tx.commit();
                return false;
            }else {
                session.remove(ob);
                tx.commit();
                return true;
            }
        }catch (RuntimeException e){
            tx.rollback();
            throw new RuntimeException(e.getMessage());
        }finally {
            session.close();
        }

    }

    @Override
    public List<Comment> getByQuery(String hql) {
        Session session=sessionFactory.openSession();
        Transaction tx=session.beginTransaction();
        try{
            List<Comment> l=session.createQuery(hql,Comment.class).getResultList();
            tx.commit();
            return l;
        }catch(RuntimeException ex){
            tx.rollback();
            throw new RuntimeException(ex);
        }finally {
            session.close();
        }
    }
}
