package org.example.service;

import org.example.event.Operation;
import org.example.event.UserEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(emailService, "from", "no-reply@test.com");
    }

    @Test
    @DisplayName("должен отправить письмо при создании пользователя")
    public void shouldSendCreateEmail() {
        UserEvent event = new UserEvent(Operation.CREATE, "reply@test.com");
        emailService.sendEmail(event);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("no-reply@test.com", message.getFrom());
        assertEquals("reply@test.com", message.getTo()[0]);
        assertEquals("Добро пожаловать!", message.getSubject());
        assertTrue(message.getText().contains("Ваш аккаунт на сайте Microservices Project был успешно создан."));
    }

    @Test
    @DisplayName("должен отправить письмо при удалении пользователя")
    public void shouldSendDeleteEmail() {
        UserEvent event = new UserEvent(Operation.DELETE, "reply@test.com");
        emailService.sendEmail(event);
        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        assertEquals("no-reply@test.com", message.getFrom());
        assertEquals("reply@test.com", message.getTo()[0]);
        assertEquals("Аккаунт удален", message.getSubject());
        assertTrue(message.getText().contains("Ваш аккаунт был удален."));
    }

}
