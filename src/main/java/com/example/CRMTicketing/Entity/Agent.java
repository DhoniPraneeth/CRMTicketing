package com.example.CRMTicketing.Entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "Agent")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Agent {
    @Id
    private Integer agentId;
    @Column(nullable = false)
    @NotNull
    private String agentName;
    @Email
    private String email;
    private Boolean availabilityStatus;
    @Column(name = "active_ticket_count")
    private Integer activeTicketCount;
    @OneToMany(mappedBy = "agent",
            cascade = CascadeType.ALL,
            orphanRemoval = true)
    private List<Ticket> tickets;
}
