package com.example.CRMTicketing.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AgentRequestDTO {
    @Null
    private String agentId;
    @Size(min = 3, max = 50)
    private String name;
    @NotBlank(message = "Department is required")
    private String Department;
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email")
    private String email;
    private boolean available;
}