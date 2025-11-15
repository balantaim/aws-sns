package com.martinatanasov.aws.sns.controllers;

import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.martinatanasov.aws.sns.services.NotificationService;
import com.martinatanasov.aws.sns.services.SubscriptionService;
import com.martinatanasov.aws.sns.util.EmailValidatorUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {

    @Value("${app.phone.default-region}")
    private String DEFAULT_REGION;
    private final NotificationService notificationService;
    private final SubscriptionService subscriptionService;
    private final EmailValidatorUtil emailValidator;
    private final PhoneNumberUtil phoneNumberUtil = PhoneNumberUtil.getInstance();

    // Send messages to all subscribers (including phone number subscribers)
    @PostMapping("/send-notification/{message}")
    public ResponseEntity<String> sendNotificationMessageToSubscribers(@PathVariable String message) {
        if (isMessageValid(message)) {
            notificationService.sendNotification(message);
            return new ResponseEntity<>("Notification send!", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid message!", HttpStatus.BAD_REQUEST);
    }

    // Email
    // Subscribe Email
    @PostMapping("/subscribe-email/{email}")
    public ResponseEntity<String> subscribeEmail(@PathVariable String email) {
        if (emailValidator.isEmailValid(email)) {
            subscriptionService.subscribeEmail(email);
            return new ResponseEntity<>("Email added for verification: " + email, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid email!", HttpStatus.BAD_REQUEST);
    }

    // Unsubscribe Email
    @DeleteMapping("/unsubscribe-email/{email}")
    public ResponseEntity<String> unsubscribeEmail(@PathVariable String email) {
        boolean isUnsubscribed;
        if (emailValidator.isEmailValid(email)) {
            isUnsubscribed = subscriptionService.unsubscribe(email);
        } else {
            return new ResponseEntity<>("Invalid email!", HttpStatus.BAD_REQUEST);
        }
        if (isUnsubscribed) {
            return new ResponseEntity<>("Email unsubscribed!", HttpStatus.OK);
        }
        return new ResponseEntity<>("No subscription found!", HttpStatus.BAD_REQUEST);
    }

    // Phone
    // Subscribe Phone number
    @PostMapping("/subscribe-phone/{phone}")
    public ResponseEntity<String> subscribePhone(@PathVariable String phone) throws NumberParseException {
        if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phone, DEFAULT_REGION))) {
            subscriptionService.subscribeMobileNumber(phone);
            return new ResponseEntity<>("Phone added for verification: " + phone, HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid phone!", HttpStatus.BAD_REQUEST);
    }

    // Send SMS with message to specific phone number
    @PostMapping("/sms/{phone}/{message}")
    public ResponseEntity<String> sendSms(@PathVariable String phone, @PathVariable String message) throws NumberParseException {
        if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phone, DEFAULT_REGION)) && isMessageValid(message)) {
            notificationService.sendSms(phone, message);
            return new ResponseEntity<>("SMS sent!", HttpStatus.CREATED);
        }
        return new ResponseEntity<>("Invalid data!", HttpStatus.BAD_REQUEST);
    }

    // Unsubscribe phone number
    @DeleteMapping("/unsubscribe-phone/{phone}")
    public ResponseEntity<String> unsubscribePhone(@PathVariable String phone) throws NumberParseException {
        boolean isUnsubscribed;
        if (phoneNumberUtil.isValidNumber(phoneNumberUtil.parse(phone, DEFAULT_REGION))) {
            isUnsubscribed = subscriptionService.unsubscribe(phone);
        } else {
            return new ResponseEntity<>("Invalid phone!", HttpStatus.BAD_REQUEST);
        }
        if (isUnsubscribed) {
            return new ResponseEntity<>("Phone number unsubscribed!", HttpStatus.OK);
        }
        return new ResponseEntity<>("No subscription found!", HttpStatus.BAD_REQUEST);
    }

    private boolean isMessageValid(final String message) {
        return message != null && (message.length() > 3 && message.length() <= 255);
    }

}
