package com.example.CRMTicketing.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "comment")
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    @JsonIgnore
    private Long comment_id;

    @NotNull
    @NotBlank
    @Column(name = "message")
    private String message;

    @JsonIgnore
    @Column(name = "fk_ticket_id")
    private Long ticket_id;
}
