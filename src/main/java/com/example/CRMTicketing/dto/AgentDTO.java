package com.example.CRMTicketing.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class AgentDTO {
    private Long agentId;

    @NotBlank(message = "Agent name is required")
    private String agentName;

    @NotBlank(message = "Agent email is required")
    @Email(message = "Agent email must be valid")
    private String email;

    private String avaialbleStatus;
}
