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
    @Column(
            name="comment_id"
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long commentId;
    @Column(nullable = false,
            columnDefinition = "TEXT",
            name = "message")
    private String message;
    @Column(
            name = "commented_by"
    )
    private String commentedBy;
    @Column(
            name = "created_at"
    )
    private LocalDateTime createdAt;
    // Many Comments -> One Ticket
    @Column(
            name = "ticket_id"
    )
    private Long ticketId;
}
