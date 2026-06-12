package com.example.CRMTicketing.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoryEvent {

    private String objectType;

    private String objectId;

    private String action;
}