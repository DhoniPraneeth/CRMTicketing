package com.example.CRMTicketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {

    @NotBlank(message = "Comment message is required")
    @Size(min = 2, max = 500)
    private String message;

    @NotNull(message = "Ticket ID required")
    private Long ticketId;

    @NotNull(message = "Agent ID required")
    private Long agentId;
}
