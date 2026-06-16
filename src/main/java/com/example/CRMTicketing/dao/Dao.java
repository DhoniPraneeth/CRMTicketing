package com.example.CRMTicketing.dao;

import com.example.CRMTicketing.Entity.Comment;

import java.util.List;
public interface Dao<T> {
    void saveOrUpdate(T obj);
    T getById(Class<T> type,Long id);
    List<T> get(Class<T> type,Integer limit,Integer offset);
    boolean deleteById(Long id,Class<T> type);

    List<Comment> getByQuery(String hql);
}
