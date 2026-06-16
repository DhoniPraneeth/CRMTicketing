package com.example.CRMTicketing.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "agent")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    @NotNull
    private Long id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    @JsonIgnore
    private Long agentId;
    @Column(nullable = false, name = "agent_name")
    @NotNull(message = "name shouldn't be null")
    @NotBlank(message = "agent name shouldn't be blank")
    private String agentName;

    @Column(name = "email")
    @Email(message = "email shoud be in email format")
    @NotNull(message = "Email shouldn't be empty :)")
    @NotBlank(message = "Email shouldn't be Blank :)")
    private String email;

    @Column(
            name = "availability_status"
    )
    private Boolean availabilityStatus;

    @JsonIgnore
    @Column(
            name = "active_ticket_count"
    )
    private Integer activeTicketCount;

    @Transient
    @JsonIgnore
    private List<Long> tickets;
}
