package com.example.edutrack.transactions.model;

import java.util.Date;
import java.util.UUID;

public interface CommonTransaction {
    UUID getTransactionId();
    String getTransactionInfo();
    Double getTransactionAmount();
    String getTransactionStatus();
    Date getTransactionDate();
    UUID getUserId();
}
