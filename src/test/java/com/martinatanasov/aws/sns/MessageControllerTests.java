package com.martinatanasov.aws.sns;

import com.martinatanasov.aws.sns.util.EmailValidatorUtil;
import com.martinatanasov.aws.sns.controllers.MessageController;
import com.martinatanasov.aws.sns.services.NotificationService;
import com.martinatanasov.aws.sns.services.SubscriptionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest({MessageController.class, EmailValidatorUtil.class})
public class MessageControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private SubscriptionService subscriptionService;

    // Test data
    private final String email = "test-email@gmail.com";
    private final String phone = "+359123456789";

    // Success messages
    @Test
    void testSendNotificationToAll() throws Exception {
        mockMvc.perform(post("/send-notification/HelloSubscribers"))
                .andExpect(status().isCreated())
                .andExpect(content().string("Notification send!"));
    }

    @Test
    void testSubscribeEmail() throws Exception {
        mockMvc.perform(post("/subscribe-email/" + email))
                .andExpect(status().isCreated())
                .andExpect(content().string("Email added for verification: " + email));
    }

    @Test
    void testUnsubscribeEmail() throws Exception {
        mockMvc.perform(delete("/unsubscribe-email/" + email))
                .andExpect(status().isOk())
                .andExpect(content().string("Email unsubscribed!"));
    }

    @Test
    void testSubscribePhone() throws Exception {
        mockMvc.perform(post("/subscribe-phone/" + phone))
                .andExpect(status().isCreated())
                .andExpect(content().string("Phone added for verification: " + phone));
    }

    @Test
    void testSendSms() throws Exception {
        mockMvc.perform(post("/sms/" + phone + "/Hello"))
                .andExpect(status().isCreated())
                .andExpect(content().string("SMS sent!"));
    }

    @Test
    void testUnsubscribePhone() throws Exception {
        mockMvc.perform(delete("/unsubscribe-phone/" + phone))
                .andExpect(status().isOk())
                .andExpect(content().string("Phone number unsubscribed!"));
    }

    // Error messages
    @Test
    void testSendSmsInvalidMessage() throws Exception {
        mockMvc.perform(post("/sms/" + phone + "/Hi"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid data!"));
    }

    @Test
    void testSubscribePhoneInvalidPhone() throws Exception {
        mockMvc.perform(post("/subscribe-phone/" + "1 1234567890"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid phone!"));
    }

    @Test
    void testSubscribeEmailInvalidEmail() throws Exception {
        mockMvc.perform(post("/subscribe-email/" + "abv@bg"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Invalid email!"));
    }

}
