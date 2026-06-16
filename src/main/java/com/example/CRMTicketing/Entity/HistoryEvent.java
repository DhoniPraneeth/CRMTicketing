package com.example.CRMTicketing.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HistoryEvent {
    String objectType;
    String objectId;
    String action;
}
