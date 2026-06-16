package com.example.CRMTicketing.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean //Bean for creation of new topic
    public NewTopic historyTopic() {
        return TopicBuilder
                .name("history-events")
                .partitions(1)
                .replicas(1)
                .build();
    }
}