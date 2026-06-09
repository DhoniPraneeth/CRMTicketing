package com.example.CRMTicketing.DTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    private Long id;
    @NotBlank(message = "Comment message is required")
    @Size(min = 2, max = 500, message = "Comment must be between 2 and 500 characters")
    private String message;
    @NotNull(message = "Ticket ID is required")
    private Long ticketId;
    @NotNull(message = "Agent ID is required")
    private Long agentId;
}