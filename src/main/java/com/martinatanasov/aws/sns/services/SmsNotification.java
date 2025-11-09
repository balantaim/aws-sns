package com.martinatanasov.aws.sns.services;

public interface SmsNotification {

    void sendSms(String phoneNumber, String message);

}
