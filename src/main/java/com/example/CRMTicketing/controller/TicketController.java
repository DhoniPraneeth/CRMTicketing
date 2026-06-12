package com.example.CRMTicketing.controller;
import com.example.CRMTicketing.dto.AgentDTO;
import com.example.CRMTicketing.dto.TicketDTO;
import com.example.CRMTicketing.exception.BadRequestException;
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
    // Create Ticket
    @PostMapping
    public ResponseEntity<String> createTicket(@Valid @NotNull @RequestBody TicketDTO dto) {
        ticketService.save(dto);
        log.info("Ticket created successfully");
        return ResponseEntity.ok("Ticket is saved");
    }

    // Get Ticket By Id
    @GetMapping("/{id}")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable @NotNull Long id) {
        log.info("Fetching ticket by id: {}", id);
        return ResponseEntity.ok(ticketService.getById(id));
    }

    // Get All Tickets
    @GetMapping
    public ResponseEntity<List<TicketDTO>> getAllTickets() {
        log.info("Fetching all tickets");
        return ResponseEntity.ok(
                ticketService.getAllTickets());
    }

    // Update Ticket
    @PutMapping("/{id}")
    public ResponseEntity<TicketDTO> updateTicket(
            @PathVariable @NotNull Long id,
            @Valid @NotNull @RequestBody TicketDTO dto) {
        log.info("Updating ticket id: {}", id);
        return ResponseEntity.ok(ticketService.update(id, dto));
    }

    // Delete Ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteTicket(@PathVariable @NotNull Long id) {
        log.info("Deleting ticket id: {}", id);
        ticketService.delete(id);
        return ResponseEntity.ok("Ticket deleted successfully");
    }

    // Assign Agent
    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<String> assignAgent(@PathVariable @NotNull Long ticketId,
            @NotNull @RequestBody AgentDTO dto) {
        if (dto.getAgentId() == null) {
            throw new BadRequestException("Agent id is required for assignment");
        }
        log.info("Assigning agent id {} to ticket id {}", dto.getAgentId(), ticketId);
        ticketService.assignAgent(ticketId, dto.getAgentId());
        return ResponseEntity.ok("Agent assigned successfully");
    }

    // Resolve Ticket
    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<String> resolveTicket(@PathVariable @NotNull Long ticketId) {
        log.info("Resolving ticket id: {}", ticketId);
        ticketService.resolveTicket(ticketId);
        return ResponseEntity.ok("Ticket resolved successfully");
    }

    // Close Ticket
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<String> closeTicket(@PathVariable @NotNull Long ticketId) {
        log.info("Closing ticket id: {}", ticketId);
        ticketService.closeTicket(ticketId);
        return ResponseEntity.ok("Ticket closed successfully");
    }
}