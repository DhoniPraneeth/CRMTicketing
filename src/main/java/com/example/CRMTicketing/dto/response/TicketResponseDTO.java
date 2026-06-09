package com.example.CRMTicketing.dto.response;

import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketResponseDTO {

    private Long id;
    private String title;
    private String description;
    private Priority priority;
    private TicketStatus status;
    private String agentName;
}
