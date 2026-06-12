package com.example.CRMTicketing.dto;

import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketDTO {
    private Long ticketId;
    
    @NotBlank(message = "Ticket title is required")
    private String title;

    private String description;
    private String category;

    @NotNull(message = "Ticket priority is required")
    private Priority priority;

    private TicketStatus ticketStatus;
}
