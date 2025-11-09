package com.martinatanasov.aws.sns.controllers;

import com.martinatanasov.aws.sns.services.NotificationServiceImpl;
import com.martinatanasov.aws.sns.services.SubscriptionServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    private final NotificationServiceImpl notificationService;
    private final SubscriptionServiceImpl subscriptionService;

    // Email
    @PostMapping("/send-email/{message}")
    public ResponseEntity<String> sendEmailMessage(@PathVariable String message) {
        if (isMessageValid(message)) {
            notificationService.sendEmailNotification(message);
            return new ResponseEntity<>("Email send!", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid message!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/subscribe-email/{email}")
    public ResponseEntity<String> subscribeEmail(@PathVariable String email) {
        if (email != null && email.contains("@")) {
            subscriptionService.subscribeEmail(email);
            return new ResponseEntity<>("Email added for verification: " + email, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid email!", HttpStatus.BAD_REQUEST);
    }

    // Phone
    @PostMapping("/subscribe-phone/{phone}")
    public ResponseEntity<String> subscribePhone(@PathVariable String phone) {
        if (phone != null && phone.startsWith("+359")) {
            subscriptionService.subscribeMobileNumber(phone);
            return new ResponseEntity<>("Phone added for verification: " + phone, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid phone!", HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/sms/{phone}/{message}")
    public ResponseEntity<String> sendSms(@PathVariable String phone, @PathVariable String message) {
        if (phone != null && phone.startsWith("+359") && isMessageValid(message)) {
            notificationService.sendSms(phone, message);
            return new ResponseEntity<>("SMS sent!", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid data!", HttpStatus.BAD_REQUEST);
    }

    private boolean isMessageValid(final String message) {
        return message != null && (message.length() > 3 && message.length() <= 255);
    }

}
