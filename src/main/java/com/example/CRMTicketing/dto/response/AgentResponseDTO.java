package com.example.CRMTicketing.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentResponseDTO {
    private Long id;
    private String name;
    private String email;
    private String department;
    private boolean available;
}