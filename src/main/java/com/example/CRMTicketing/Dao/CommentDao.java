package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Comment;

import java.util.List;

public interface CommentDao {

    void save(Comment comment);

    Comment getById(Integer id);

    List<Comment> getAllComments();

    List<Comment> getCommentsByTicketId(Integer ticketId);

    void update(Comment comment);

    void delete(Integer id);
}