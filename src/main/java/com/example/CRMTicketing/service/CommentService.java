package com.example.CRMTicketing.service;

import com.example.CRMTicketing.Dao.CommentDaoImpl;
import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.dto.CommentDTO;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.exception.ResourceNotFoundException;
import com.example.CRMTicketing.mapper.CommentMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class CommentService {

    private final CommentDaoImpl commentDao;
    private final CommentMapper commentMapper;

    public CommentDTO save(CommentDTO dto) {
        validateCommentDto(dto);
        Comment comment = commentMapper.toEntity(dto);
        comment.setCreatedAt(LocalDateTime.now());
        commentDao.save(comment);
        return commentMapper.toDTO(comment);
    }

    public CommentDTO getById(Integer id) {
        validateId(id, "Comment id is required");
        Comment comment = commentDao.getById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found with id " + id);
        }
        return commentMapper.toDTO(comment);
    }

    public List<CommentDTO> getAllComments() {
        return commentDao.getAllComments().stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public List<CommentDTO> getCommentsByTicketId(Integer ticketId) {
        validateId(ticketId, "Ticket id is required");
        return commentDao.getCommentsByTicketId(ticketId).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public void delete(Integer id) {
        validateId(id, "Comment id is required");
        Comment comment = commentDao.getById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found with id " + id);
        }
        commentDao.delete(id);
    }

    private void validateCommentDto(CommentDTO dto) {
        if (dto == null) {
            throw new BadRequestException("Comment payload is required");
        }
        if (dto.getMessage() == null || dto.getMessage().isBlank()) {
            throw new BadRequestException("Comment message is required");
        }
    }

    private void validateId(Integer id, String message) {
        if (id == null) {
            throw new BadRequestException(message);
        }
    }
}
