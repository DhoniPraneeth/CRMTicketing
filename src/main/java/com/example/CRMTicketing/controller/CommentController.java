package com.example.CRMTicketing.controller;
import com.example.CRMTicketing.dto.CommentDTO;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.service.CommentService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/comments")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class CommentController {

    private final CommentService commentService;

    // Add Comment
    @PostMapping
    public ResponseEntity<CommentDTO> createComment(@Valid @NotNull @RequestBody CommentDTO dto) {
        log.info("Creating comment for ticket id: {}", dto.getTicketId());
        return ResponseEntity.ok(commentService.save(dto));
    }

    // Get Comment By Id
    @GetMapping("/{id}")
    public ResponseEntity<CommentDTO> getCommentById(@PathVariable @NotNull Integer id) {
        log.info("Fetching comment by id: {}", id);
        return ResponseEntity.ok(commentService.getById(id));
    }

    // Get All Comments
    @GetMapping
    public ResponseEntity<List<CommentDTO>> getAllComments() {
        log.info("Fetching all comments");
        return ResponseEntity.ok(commentService.getAllComments());
    }
    // Get Comments By Ticket Id
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentDTO>> getCommentsByTicketId(@PathVariable @NotNull Integer ticketId) {
        log.info("Fetching comments for ticket id: {}", ticketId);
        return ResponseEntity.ok(commentService.getCommentsByTicketId(ticketId));
    }

    // Delete Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteComment(@PathVariable @NotNull Integer id) {
        log.info("Deleting comment id: {}", id);
        commentService.delete(id);
        return ResponseEntity.ok("Comment deleted successfully");
    }
}