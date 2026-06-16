package com.example.CRMTicketing.Enums;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TicketStatus {
    OPEN,
    ASSIGNED,
    IN_PROGRESS,
    RESOLVED,
    CLOSED,
    ESCALATED,
    SLA_BREACHED;

    @JsonCreator
    public static Priority fromString(String value) {
        return Priority.valueOf(value.toUpperCase());
    }
}
