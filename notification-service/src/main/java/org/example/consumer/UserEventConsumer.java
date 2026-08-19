package org.example.consumer;

import org.example.event.UserEvent;
import org.example.service.EmailService;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class UserEventConsumer {

    private final EmailService service;

    public UserEventConsumer(EmailService service) {
        this.service = service;
    }

    @KafkaListener(topics = "${kafka.topic.user-events}")
    public void listen(UserEvent event) {
        service.sendEmail(event);
    }
}
