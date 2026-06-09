package com.example.CRMTicketing.mapper;

import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.dto.request.CommentRequestDTO;
import com.example.CRMTicketing.dto.response.CommentResponseDTO;
import org.springframework.stereotype.Component;

@Component
public class CommentMapper {

    public Comment toEntity(CommentRequestDTO dto) {

        Comment comment = new Comment();

        comment.setMessage(dto.getMessage());

        return comment;
    }

    public CommentResponseDTO toResponseDTO(Comment comment) {

        CommentResponseDTO dto = new CommentResponseDTO();

        dto.setId(comment.getCommentId());
        dto.setMessage(comment.getMessage());

        if (comment.getTicket() != null) {
            dto.setTicketId(Long.valueOf(comment.getTicket().getTicketId()));
        }

        return dto;
    }
}