package com.example.CRMTicketing.controller;
import com.example.CRMTicketing.dto.request.CommentRequestDTO;
import com.example.CRMTicketing.dto.response.CommentResponseDTO;
import com.example.CRMTicketing.service.CommentService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class CommentController {

    private final CommentService commentService;

    // Add Comment
    @PostMapping
    public ResponseEntity<CommentResponseDTO>
    createComment(
            @Valid
            @RequestBody
            CommentRequestDTO dto) {
        return ResponseEntity.ok(
                commentService.save(dto));
    }

    // Get Comment By Id
    @GetMapping("/{id}")
    public ResponseEntity<CommentResponseDTO>
    getCommentById(
            @PathVariable
            Long id) {

        return ResponseEntity.ok(
                commentService.getById(id));
    }

    // Get All Comments
    @GetMapping
    public ResponseEntity<List<CommentResponseDTO>>
    getAllComments() {

        return ResponseEntity.ok(
                commentService.getAllComments());
    }

    // Get Comments By Ticket Id
    @GetMapping("/ticket/{ticketId}")
    public ResponseEntity<List<CommentResponseDTO>>
    getCommentsByTicketId(

            @PathVariable
            Long ticketId) {

        return ResponseEntity.ok(
                commentService
                        .getCommentsByTicketId(
                                ticketId));
    }

    // Delete Comment
    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteComment(
            @PathVariable
            Long id) {

        commentService.delete(id);

        return ResponseEntity.ok(
                "Comment deleted successfully");
    }
}