package com.example.CRMTicketing.controller;
import com.example.CRMTicketing.Entity.Comment;
import com.example.CRMTicketing.Entity.Ticket;
import com.example.CRMTicketing.dao.Fetcher;
import com.example.CRMTicketing.exception.BadRequestException;
import com.example.CRMTicketing.service.CommentService;
import com.example.CRMTicketing.service.TicketService;
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
@RequestMapping("/tickets")
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketController {
    
    private final TicketService ticketService;
    private final CommentService commentService;
    // Create Ticket
    @PostMapping
    public ResponseEntity<String> createOrUpdateTicket(@Valid @NotNull @RequestBody Ticket dto) {
        ticketService.save(dto);
        log.info("Ticket created successfully");
        return ResponseEntity.ok("Ticket is saved");
    }

    // Get Ticket By Id
    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable @NotNull Long id) {
        log.info("Fetching ticket by id: {}", id);
        return ResponseEntity.ok(ticketService.getById(id));
    }

    // Get All Tickets
    @GetMapping
    public ResponseEntity<List<Ticket>> getAllTickets() {
        log.info("Fetching all tickets");
        return ResponseEntity.ok(
                ticketService.getAllTickets());
    }


    // Delete Ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable @NotNull Long id) {
        log.info("Deleting ticket id: {}", id);
        ticketService.delete(id);
        return ResponseEntity.ok("Ticket deleted successfully");
    }

    @PostMapping("/comment")
    public ResponseEntity<String> postComment(Long id, Comment comment){
        Ticket t=ticketService.getById(id);
        Long ticketId=t.getTicketId();
        if(t==null&&t.getTicketId()==null)
            return ResponseEntity.ok("Resource Not Found");
        if(commentService.postComment(comment,ticketId))
        return ResponseEntity.ok("Commented on Ticket"+ticketId);
        return ResponseEntity.badRequest().body("Unable to post comment");
    }
    @GetMapping("/comment")
    public ResponseEntity<?> getComments(Long id){
        List<Comment> list=commentService.get(id);
        if(list!=null)
        return ResponseEntity.ok(list);
        return ResponseEntity.badRequest().body("No Comments yet");
    }


}