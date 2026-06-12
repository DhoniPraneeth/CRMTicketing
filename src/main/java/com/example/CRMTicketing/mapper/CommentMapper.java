package com.example.CRMTicketing.mapper;

import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.dto.CommentDTO;

import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentDTO dto) {

        Comment comment = new Comment();
        comment.setMessage(dto.getMessage());
        comment.setCommentedBy(dto.getCommentedBy());
        comment.setTicketId(dto.getTicketId());
        return comment;
    }

    public CommentDTO toDTO(Comment comment) {

        CommentDTO dto = new CommentDTO();
        dto.setCommentedBy(comment.getCommentedBy());
        dto.setMessage(comment.getMessage());
        dto.setTicketId(comment.getTicketId());
        return dto;
    }


}