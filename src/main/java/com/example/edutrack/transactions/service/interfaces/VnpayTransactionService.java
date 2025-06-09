package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayTransaction;

import java.util.List;

public interface VnpayTransactionService {
    VnpayTransaction save(VnpayTransaction transaction);
    List<VnpayTransaction> findAll();
    String prepareTransactionUrl(VnpayTransaction transaction, VnpayConfig config);
}
