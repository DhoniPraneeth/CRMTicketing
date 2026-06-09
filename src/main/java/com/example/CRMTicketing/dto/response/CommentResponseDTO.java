package com.example.CRMTicketing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class CommentResponseDTO {

    private Long id;
    private String message;
    private Long ticketId;
    private String agentName;
}
