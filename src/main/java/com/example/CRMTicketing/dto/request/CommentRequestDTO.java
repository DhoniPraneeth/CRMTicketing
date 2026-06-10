package com.example.CRMTicketing.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentRequestDTO {
    @Null
    private String commentId;
    @NotBlank(message = "Comment message is required")
    @Size(min = 2, max = 500)
    private String message;
    @NotNull(message = "Ticket ID required")
    private Long ticketId;
    @NotNull(message = "Agent ID required")
    private Long agentId;
}
