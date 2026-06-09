package com.example.CRMTicketing.service;
import com.example.CRMTicketing.Dao.CommentDao;
import com.example.CRMTicketing.Dao.CommentDaoImpl;
import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.dto.request.CommentRequestDTO;
import com.example.CRMTicketing.dto.response.CommentResponseDTO;
import com.example.CRMTicketing.mapper.CommentMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CommentService {

    private final CommentDaoImpl commentDao;

    @Autowired
    private CommentMapper commentMapper;

    
    public CommentResponseDTO save(
            CommentRequestDTO dto) {

        Comment comment =
                commentMapper.toEntity(dto);

        commentDao.save(comment);

        return commentMapper.toResponseDTO(comment);
    }

    
    public CommentResponseDTO getById(
            Long id) {

        return commentMapper
                .toResponseDTO(
                        commentDao.getById(id));
    }

    
    public List<CommentResponseDTO>
    getAllComments() {

        return commentDao
                .getAllComments()
                .stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public List<CommentResponseDTO>
    getCommentsByTicketId(
            Long ticketId) {

        return commentDao
                .getCommentsByTicketId(
                        ticketId)
                .stream()
                .map(commentMapper::toResponseDTO)
                .collect(Collectors.toList());
    }

    
    public void delete(
            Long id) {

        commentDao.delete(id);
    }
}