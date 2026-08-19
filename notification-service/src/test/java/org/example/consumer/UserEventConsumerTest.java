package org.example.consumer;

import org.example.event.UserEvent;
import org.example.service.EmailService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserEventConsumerTest {

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserEventConsumer consumer;

    @Test
    @DisplayName("должен вызвать EmailService при получении события")
    void shouldCallEmailService() {
        UserEvent userEvent = new UserEvent();
        consumer.listen(userEvent);
        verify(emailService).sendEmail(userEvent);
    }
}
