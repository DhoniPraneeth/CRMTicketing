package com.example.CRMTicketing.Entity;

import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name="ticket")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @NotNull
    private Long id;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    @JsonIgnore
    private Long ticketId;

    @NotNull
    @NotBlank
    @Column(nullable = false, name = "title")
    private String title;

    @NotNull
    @NotBlank
    @Column(name = "descr")
    private String description;

    @NotNull
    @NotBlank
    @Column(name = "category")
    private String category;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;
    @JsonIgnore
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status")
    private TicketStatus status;
    @JsonIgnore
    @Column(name = "created_at")
    private Timestamp createdAt;
    @JsonIgnore
    @Column(name = "updated_at")
    private Timestamp updatedAt;

    @Column(name = "due_date")
    private Timestamp dueDate;
    @JsonIgnore
    @Column(name = "sla_deadline")
    private Timestamp sla_deadline;

    @JsonIgnore
    @Column(name = "agent_id")
    private Long agentId;
    @JsonIgnore
    @Column(name = "sla_id")
    private Long slaConfig;
}
