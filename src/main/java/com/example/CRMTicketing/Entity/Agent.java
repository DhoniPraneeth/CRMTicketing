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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "agent_id")
    private Long agentId;
    @Column(nullable = false, name = "agent_name")
    private String agentName;
    @Column(name = "email")
    private String email;
    @Column(
            name = "availability_status"
    )
    private Boolean availabilityStatus;
    @Column(
            name = "active_ticket_count"
    )
    private Integer activeTicketCount;
    @Transient
    private List<Long> tickets;
}
