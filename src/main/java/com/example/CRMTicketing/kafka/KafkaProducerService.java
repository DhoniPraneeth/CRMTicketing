package com.example.CRMTicketing.kafka;

import com.example.CRMTicketing.Entity.HistoryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, HistoryEvent> kafkaTemplate;

    private static final String TOPIC = "history-events";

    public void publishHistoryEvent(HistoryEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event);

        System.out.println(
                "History Event Sent: "
                        + event);
    }
}