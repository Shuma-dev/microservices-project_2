package org.example.service;

import org.example.event.UserEvent;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.retry.annotation.Recover;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;


@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String from;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }


    @Retryable(
            retryFor = MailException.class,
            maxAttempts = 3,
            backoff = @Backoff(delay = 2000)
    )
    public void sendEmail(UserEvent event) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(event.getEmail());
        switch (event.getOperation()) {
            case CREATE -> fillCreateMessage(message);
            case DELETE -> fillDeleteMessage(message);
            default -> throw new IllegalArgumentException("Неизвестная операция: " + event.getOperation());
        }
        log.info("Отправка письма пользователю {}", event.getEmail());
        try {
            mailSender.send(message);
            log.info("Письмо успешно отправлено пользователю {}", event.getEmail());
        } catch (MailException e) {
            log.error("Не удалось отправить письмо пользователю {}", event.getEmail(), e);
            throw e;
        }
    }

    @Recover
    public void recover(MailException e, UserEvent event) {
        log.error("Не удалось отправить письмо пользователю {} после 3 попыток",
                event.getEmail(), e);
    }

    private void fillCreateMessage(SimpleMailMessage message) {
        message.setSubject("Добро пожаловать!");
        message.setText("Здравствуйте!\n" +
                "\n" +
                "Ваш аккаунт на сайте Microservices Project был успешно создан.");
    }

    private void fillDeleteMessage(SimpleMailMessage message) {
        message.setSubject("Аккаунт удален");
        message.setText("Здравствуйте!\n" +
                "\n" +
                "Ваш аккаунт был удален.");
    }
}