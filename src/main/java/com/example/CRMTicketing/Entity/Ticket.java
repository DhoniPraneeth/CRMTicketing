package com.example.CRMTicketing.Entity;

import com.example.CRMTicketing.Enums.Priority;
import com.example.CRMTicketing.Enums.TicketStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name="Ticket")
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {
    @Id
    private String ticketId;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String description;
    @Column(nullable = false)
    private String category;
    @Enumerated(EnumType.STRING)
    private Priority priority;
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime dueDate;
    private LocalDateTime sla_deadline;
    @OneToMany(mappedBy = "ticket")
    private List<Comment> comments;
    @ManyToOne
    @JoinColumn(name = "agent_id")
    private Agent agent;
    @ManyToOne
    @JoinColumn(name = "sla_id")
    private SLAConfig slaConfig;
}
