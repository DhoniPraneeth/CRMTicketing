package com.example.CRMTicketing.Dao;

import com.example.CRMTicketing.Entity.Comment;

import java.util.List;

public interface CommentDao {

    void save(Comment comment);

    Comment getById(Long id);

    List<Comment> getAllComments();

    List<Comment> getCommentsByTicketId(Long ticketId);

    void update(Comment comment);

    void delete(Long id);
}