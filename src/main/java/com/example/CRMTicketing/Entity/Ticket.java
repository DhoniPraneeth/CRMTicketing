package com.example.CRMTicketing.Entity;

import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Table(name="Ticket")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ticket_id")
    private Long ticketId;
    @Column(nullable = false, name = "title")
    private String title;
    @Column(name = "descr")
    private String description;
    @Column(name = "category")
    private String category;
    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private Priority priority;
    @Enumerated(EnumType.STRING)
    @Column(name = "ticket_status")
    private TicketStatus status;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    @Column(name = "due_date")
    private LocalDateTime dueDate;
    @Column(name = "sla_deadline")
    private LocalDateTime sla_deadline;

    @Column(name = "agent_id")
    private Long agentId;
    @Column(name = "sla_id")
    private Long slaConfig;
}
