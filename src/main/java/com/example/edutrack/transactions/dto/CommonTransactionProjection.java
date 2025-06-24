package com.example.edutrack.transactions.dto;

import java.util.Date;

public interface CommonTransactionProjection {
    String getId();
    String getInfo();
    Double getAmount();
    String getStatus();
    Double getBalance();
    Date getDate();
}
