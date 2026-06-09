package com.example.CRMTicketing.controller;
import com.example.CRMTicketing.dto.AssignAgentDTO;
import com.example.CRMTicketing.dto.TicketDTO;
import com.example.CRMTicketing.dto.TicketResponseDTO;
import com.example.CRMTicketing.service.TicketService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@AllArgsConstructor(onConstructor_ = @__(@Autowired))
public class TicketController {

    private final TicketService ticketService;
    // Create Ticket
    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody TicketDTO dto) {
        return ResponseEntity.ok("Ticket Created Sucessfully"
                ticketService.save(dto));
    }

    // Get Ticket By Id
    @GetMapping("/{id}")
    public ResponseEntity<?>
    getTicketById(
            @PathVariable
            Long id) {
        return ResponseEntity.ok(
                ticketService.getById(id));
    }

    // Get All Tickets
    @GetMapping
    public ResponseEntity<List<TicketResponseDTO>>
    getAllTickets() {

        return ResponseEntity.ok(
                ticketService.getAllTickets());
    }

    // Update Ticket
    @PutMapping("/{id}")
    public ResponseEntity<TicketResponseDTO>
    updateTicket(
            @PathVariable
            Long id,

            @Valid
            @RequestBody
            TicketDTO dto) {

        return ResponseEntity.ok(
                ticketService.update(
                        id,
                        dto));
    }

    // Delete Ticket
    @DeleteMapping("/{id}")
    public ResponseEntity<String>
    deleteTicket(
            @PathVariable
            Long id) {

        ticketService.delete(id);

        return ResponseEntity.ok(
                "Ticket deleted successfully");
    }

    // Assign Agent
    @PutMapping("/{ticketId}/assign")
    public ResponseEntity<String>
    assignAgent(

            @PathVariable
            Long ticketId,

            @Valid
            @RequestBody
            AssignAgentDTO dto) {

        ticketService.assignAgent(
                ticketId,
                dto.getAgentId());

        return ResponseEntity.ok(
                "Agent assigned successfully");
    }

    // Resolve Ticket
    @PutMapping("/{ticketId}/resolve")
    public ResponseEntity<String>
    resolveTicket(
            @PathVariable
            Long ticketId) {

        ticketService.resolveTicket(
                ticketId);

        return ResponseEntity.ok(
                "Ticket resolved successfully");
    }

    // Close Ticket
    @PutMapping("/{ticketId}/close")
    public ResponseEntity<String>
    closeTicket(
            @PathVariable
            Long ticketId) {

        ticketService.closeTicket(
                ticketId);

        return ResponseEntity.ok(
                "Ticket closed successfully");
    }
}
```
