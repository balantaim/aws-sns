package com.martinatanasov.aws.sns.services;

public enum ProtocolType {

    EMAIL,
    SMS;

    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
