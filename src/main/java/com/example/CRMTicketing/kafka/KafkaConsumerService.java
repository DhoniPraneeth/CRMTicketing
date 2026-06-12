package com.example.CRMTicketing.kafka;
import com.example.CRMTicketing.Dao.HistoryDao;
import com.example.CRMTicketing.Entity.History;
import com.example.CRMTicketing.dto.HistoryEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor(onConstructor_ = @__(@Autowired))
public class KafkaConsumerService {

    private final HistoryDao historyDao;

    @KafkaListener(
            topics = "history-events",
            groupId = "crm-group")
    public void consume(HistoryEvent event) {

        History history =new History();

        history.setObjectType(event.getObjectType());

        history.setObjectId(event.getObjectId());

        history.setAction(event.getAction());

        history.setCreatedAt(LocalDateTime.now());

        historyDao.save(history);

        System.out.println(
                "History Saved: "
                        + history);
    }
}