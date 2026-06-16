package com.example.CRMTicketing.Entity;

import com.example.CRMTicketing.Enums.Priority;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "sla_config")
public class SLAConfig {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        @Column(name = "sla_id")
        private Long slaId;

        @Enumerated(EnumType.STRING)
        @Column(unique = true,
                name = "priority"
        )
        private Priority priority;

        @Column(name = "response_time_hours")
        private Integer responseTimeHours;

        @Column(name = "resolution_time_hours")
        private Integer resolutionTimeHours;

        private List<Long> tickets;
}
