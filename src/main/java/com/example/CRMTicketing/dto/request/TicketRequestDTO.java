package com.example.CRMTicketing.dto.request;


import com.example.CRMTicketing.Enums.Priority;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TicketRequestDTO {

    @NotBlank(message = "Title is required")
    @Size(min = 5, max = 100)
    private String title;

    @NotBlank(message = "Description is required")
    @Size(min = 10, max = 500)
    private String description;
    private Priority priority;

    private Long agentId;
}
