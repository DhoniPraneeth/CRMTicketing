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
import com.example.CRMTicketing.cache.UnifiedCacheService;
import static com.example.CRMTicketing.cache.UnifiedCacheService.CacheType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
@Transactional
public class CommentService {

    private final CommentDaoImpl commentDao;
    private final CommentMapper commentMapper;
    private final UnifiedCacheService cacheService;

    public CommentDTO save(CommentDTO dto) {
        validateCommentDto(dto);
        Comment comment = commentMapper.toEntity(dto);
        comment.setCreatedAt(LocalDateTime.now());
        commentDao.save(comment);
        CommentDTO out = commentMapper.toDTO(comment);
        // write to Redis and populate Caffeine
        cacheService.put(UnifiedCacheService.commentKey(comment.getCommentId()), out, CacheType.REDIS);
        cacheService.put(UnifiedCacheService.commentKey(comment.getCommentId(   )), out, CacheType.CAFFEINE);
        cacheService.evict("Comment:list:ticket:" + comment.getTicketId(), CacheType.CAFFEINE);
        return out;
    }

    public CommentDTO getById(Integer id) {
        validateId(id, "Comment id is required");
        CommentDTO cached = cacheService.get(UnifiedCacheService.commentKey((long)id), CommentDTO.class, CacheType.CAFFEINE);
        if (cached != null) return cached;

        Comment comment = commentDao.getById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found with id " + id);
        }

        CommentDTO dto = commentMapper.toDTO(comment);
        cacheService.put(UnifiedCacheService.commentKey((long)id), dto, CacheType.CAFFEINE);
        cacheService.put(UnifiedCacheService.commentKey((long)id), dto, CacheType.REDIS);
        return dto;
    }

    public List<CommentDTO> getAllComments() {
        Object cached = cacheService.get("Comment:list:all", Object.class, CacheType.CAFFEINE);
        if (cached instanceof java.util.List) {
            try {
                @SuppressWarnings("unchecked")
                java.util.List<CommentDTO> list = (java.util.List<CommentDTO>) cached;
                return list;
            } catch (ClassCastException ignored) {
            }
        }

        java.util.List<CommentDTO> dtos = commentDao.getAllComments().stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
        cacheService.put("Comment:list:all", dtos, CacheType.CAFFEINE);
        return dtos;
    }

    public List<CommentDTO> getCommentsByTicketId(Integer ticketId) {
        validateId(ticketId, "Ticket id is required");
        String listKey = "Comment:list:ticket:" + ticketId;
        Object cached = cacheService.get(listKey, Object.class, CacheType.CAFFEINE);
        if (cached instanceof java.util.List) {
            try {
                @SuppressWarnings("unchecked")
                java.util.List<CommentDTO> list = (java.util.List<CommentDTO>) cached;
                return list;
            } catch (ClassCastException ignored) {
            }
        }

        java.util.List<CommentDTO> dtos = commentDao.getCommentsByTicketId(ticketId).stream()
                .map(commentMapper::toDTO)
                .collect(Collectors.toList());
        cacheService.put(listKey, dtos, CacheType.CAFFEINE);
        return dtos;
    }

    public void delete(Integer id) {
        validateId(id, "Comment id is required");
        Comment comment = commentDao.getById(id);
        if (comment == null) {
            throw new ResourceNotFoundException("Comment not found with id " + id);
        }
        commentDao.delete(id);
        cacheService.evict(UnifiedCacheService.commentKey((long)id), CacheType.CAFFEINE);
        cacheService.evict(UnifiedCacheService.commentKey((long)id), CacheType.REDIS);
        cacheService.evict("Comment:list:all", CacheType.CAFFEINE);
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
