package com.example.CRMTicketing.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentDTO {
    @NotBlank(message = "Comment message is required")
    private String message;

    @NotBlank(message = "Commented by is required")
    private String commentedBy;

    @NotNull(message = "Ticket id is required")
    private Long ticketId;
}
