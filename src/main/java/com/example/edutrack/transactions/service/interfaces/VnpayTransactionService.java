package com.example.edutrack.transactions.service.interfaces;

import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayTransaction;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface VnpayTransactionService {
    VnpayTransaction save(VnpayTransaction transaction);
    List<VnpayTransaction> findAll();
    String prepareTransactionUrl(VnpayTransaction transaction, VnpayConfig config);
    Optional<VnpayTransaction> finalizeTransaction(Map<String, String> params);
}
