package com.example.edutrack.transactions.service;

import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.repository.VnpayTransactionRepository;
import com.example.edutrack.transactions.service.interfaces.VnpayTransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class VnpayTransactionImpl implements VnpayTransactionService {
    private final VnpayTransactionRepository vnpayTransactionRepository;

    @Autowired
    public VnpayTransactionImpl(VnpayTransactionRepository vnpayTransactionRepository) {
        this.vnpayTransactionRepository = vnpayTransactionRepository;
    }

    @Override
    public VnpayTransaction save(VnpayTransaction transaction) {
        return vnpayTransactionRepository.save(transaction);
    }

    @Override
    public List<VnpayTransaction> findAll() {
        return vnpayTransactionRepository.findAll();
    }

    @Override
    public String prepareTransactionUrl(VnpayTransaction transaction, VnpayConfig config) {

        Map<String, String> params = getParamMap(transaction, config);

        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();

        Iterator<String> iter = fieldNames.iterator();
        while (iter.hasNext()) {
            String fieldName = iter.next();
            String fieldValue = params.get(fieldName);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                hashData.append(fieldName);
                hashData.append("=");
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append("=");
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));

                if (iter.hasNext()) {
                    hashData.append("&");
                    query.append("&");
                }
            }
        }

        String queryUrl = query.toString();
        String secureHash = config.hmacSHA512(config.vnpHashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + secureHash;

        return config.vnpApiUrl + "?" + queryUrl;
    }

    @Override
    public Optional<VnpayTransaction> finalizeTransaction(Map<String, String> params) {
        Optional<VnpayTransaction> transactionOptional = vnpayTransactionRepository.findById(UUID.fromString(params.get("vnp_TxnRef")));
        if (transactionOptional.isEmpty()) {
            return Optional.empty();
        }

        VnpayTransaction transaction = transactionOptional.get();
        transaction.setBankCode(params.get("vnp_BankCode"));
        transaction.setBankTranNo(params.get("vnp_BankTranNo"));
        transaction.setCardType(params.get("vnp_CardType"));
        transaction.setTransactionNo(params.get("vnp_TransactionNo"));
        transaction.setPayDate(params.get("vnp_PayDate"));
        transaction.setResponseCode(params.get("vnp_ResponseCode"));
        transaction.setTransactionStatus(params.get("vnp_TransactionStatus"));

        return Optional.of(vnpayTransactionRepository.save(transaction));
    }

    private static Map<String, String> getParamMap(VnpayTransaction transaction, VnpayConfig config) {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_Version", transaction.getVersion());
        params.put("vnp_Command", transaction.getCommand());
        params.put("vnp_TmnCode", config.vnpTmnCode);
        params.put("vnp_Amount", String.valueOf(transaction.getAmount()));
//        params.put("vnp_BankCode", transaction.getBankCode());
        params.put("vnp_CreateDate", transaction.getCreatedDate());
        params.put("vnp_CurrCode", transaction.getCurrCode());
        params.put("vnp_IpAddr", transaction.getIpAddress());
        params.put("vnp_Locale", transaction.getLocale());
        params.put("vnp_OrderInfo", transaction.getOrderInfo());
        params.put("vnp_OrderType", transaction.getOrderType());
        params.put("vnp_ReturnUrl", config.vnpReturnUrl);
        params.put("vnp_ExpireDate", transaction.getExpireDate());
        params.put("vnp_TxnRef", String.valueOf(transaction.getTxnRef()));
        return params;
    }
}
