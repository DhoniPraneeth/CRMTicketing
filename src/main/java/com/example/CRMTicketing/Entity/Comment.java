package com.example.CRMTicketing.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "Comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;

    @Column(nullable = false,
            columnDefinition = "TEXT")
    private String message;
    private String commentedBy;
    private LocalDateTime createdAt;
    // Many Comments -> One Ticket
    @ManyToOne
    @JoinColumn(name = "ticket_id")
    private Ticket ticket;
}
