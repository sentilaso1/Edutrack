package com.example.edutrack.transactions.service;

import com.example.edutrack.common.model.Randomizer;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.exceptions.FinalizedTransactionException;
import com.example.edutrack.transactions.model.VnpayPayTransaction;
import com.example.edutrack.transactions.model.VnpayRefundTransaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.repository.VnpayPayTransactionRepository;
import com.example.edutrack.transactions.repository.VnpayRefundTransactionRepository;
import com.example.edutrack.transactions.repository.VnpayTransactionRepository;
import com.google.gson.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class VnpayTransactionService {
    private final VnpayTransactionRepository commonRepository;
    private final VnpayPayTransactionRepository payRepository;
    private final VnpayRefundTransactionRepository refundRepository;
    private final VnpayConfig config;

    private static final DateTimeFormatter TIMESTAMP_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    public VnpayTransactionService(VnpayTransactionRepository commonRepository, VnpayPayTransactionRepository payRepository, VnpayRefundTransactionRepository refundRepository, VnpayConfig config) {
        this.commonRepository = commonRepository;
        this.payRepository = payRepository;
        this.refundRepository = refundRepository;
        this.config = config;
    }

    public Optional<VnpayPayTransaction> findPayTransaction(String txnRef) {
        return payRepository.findByTxnRef(txnRef);
    }

    public Optional<VnpayRefundTransaction> findRefundTransaction(String txnRef) {
        return refundRepository.findByTxnRef(txnRef);
    }

    public VnpayTransaction save(VnpayPayTransaction transaction) {
        return payRepository.save(transaction);
    }

    public VnpayTransaction save(VnpayRefundTransaction transaction) {
        return refundRepository.save(transaction);
    }

    public static String generateTransactionCode() {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMATTER);
        String randomPart = Randomizer.randomAlphanumeric(6);
        return timestamp + randomPart;
    }

    public VnpayPayTransaction createBasePayTransaction() {
        VnpayPayTransaction transaction = new VnpayPayTransaction();
        //noinspection DataFlowIssue
        transaction = (VnpayPayTransaction) createBaseTransaction(transaction);

        transaction.setCurrCode("VND");
        transaction.setLocale("vn");
        transaction.setOrderType(VnpayTransaction.ORDER_TYPE_OTHER);
        transaction.setReturnUrl(config.vnpReturnUrl);
        transaction.setTxnRef(generateTransactionCode());

        Date createdDate = transaction.calendar.getTime();
        transaction.calendar.add(Calendar.MINUTE, VnpayTransaction.TRANSACTION_EXPIRATION_TIME);
        Date expireDate = transaction.calendar.getTime();

        transaction.setCreateDate(transaction.dateFormat.format(createdDate));
        transaction.setExpireDate(transaction.dateFormat.format(expireDate));

        return transaction;
    }

    public VnpayRefundTransaction createBaseRefundTransaction() {
        VnpayRefundTransaction transaction = new VnpayRefundTransaction();
        //noinspection DataFlowIssue
        transaction = (VnpayRefundTransaction) createBaseTransaction(transaction);

        transaction.setRequestId(generateTransactionCode());
        transaction.setTransactionType(VnpayRefundTransaction.TYPE_FULL_REFUND);
        Date createdDate = transaction.calendar.getTime();
        transaction.setCreateDate(transaction.dateFormat.format(createdDate));

        return transaction;
    }

    private VnpayTransaction createBaseTransaction(VnpayTransaction transaction) {
        transaction.setVersion("2.1.0");

        if (transaction instanceof VnpayPayTransaction) {
            transaction.setCommand(VnpayTransaction.COMMAND_PAY);
        } else if (transaction instanceof VnpayRefundTransaction) {
            transaction.setCommand(VnpayTransaction.COMMAND_REFUND);
        }

        transaction.setTmnCode(config.vnpTmnCode);
        return transaction;
    }

    public String preparePayUrl(VnpayPayTransaction transaction) {
        Map<String, String> params = getPayParamMap(transaction);

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

    public Optional<VnpayPayTransaction> finalizePayTransaction(Map<String, String> params) {
        Optional<VnpayPayTransaction> transactionOptional = payRepository.findByTxnRef(params.get("vnp_TxnRef"));
        if (transactionOptional.isEmpty()) {
            return Optional.empty();
        }

        VnpayPayTransaction transaction = transactionOptional.get();
        if (transaction.getDone()) {
            throw new FinalizedTransactionException();
        }

        transaction.setBankCode(params.get("vnp_BankCode"));
        transaction.setBankTranNo(params.get("vnp_BankTranNo"));
        transaction.setCardType(params.get("vnp_CardType"));
        transaction.setTransactionNo(params.get("vnp_TransactionNo"));
        transaction.setPayDate(params.get("vnp_PayDate"));
        transaction.setResponseCode(params.get("vnp_ResponseCode"));
        transaction.setTransactionStatus(params.get("vnp_TransactionStatus"));
        transaction.setDone(true);

        return Optional.of(payRepository.save(transaction));
    }

    public JsonObject prepareRefundJson(VnpayRefundTransaction transaction) {
        JsonObject p = new JsonObject();

        p.addProperty("vnp_RequestId", transaction.getRequestId());
        p.addProperty("vnp_Version", transaction.getVersion());
        p.addProperty("vnp_Command", transaction.getCommand());
        p.addProperty("vnp_TmnCode", transaction.getTmnCode());
        p.addProperty("vnp_TransactionType", transaction.getTransactionType());
        p.addProperty("vnp_TxnRef", transaction.getTxnRef());
        p.addProperty("vnp_OrderInfo", transaction.getOrderInfo());
        p.addProperty("vnp_TransactionNo", transaction.getTransactionNo());
        p.addProperty("vnp_TransactionDate", transaction.getTransactionDate());
        p.addProperty("vnp_CreateBy", transaction.getCreateBy());
        p.addProperty("vnp_CreateDate", transaction.getCreateDate());
        p.addProperty("vnp_IpAddr", transaction.getIpAddr());
        p.addProperty("vnp_Amount", transaction.getAmount());

        String hashData = String.join("|",
                transaction.getRequestId(),
                transaction.getVersion(),
                transaction.getCommand(),
                transaction.getTmnCode(),
                transaction.getTransactionType(),
                transaction.getTxnRef(),
                String.valueOf(transaction.getAmount()),
                transaction.getTransactionNo(),
                transaction.getTransactionDate(),
                transaction.getCreateBy(),
                transaction.getCreateDate(),
                transaction.getIpAddr(),
                transaction.getOrderInfo()
        );

        String secureHash = config.hmacSHA512(config.vnpHashSecret, hashData);
        p.addProperty("vnp_SecureHash", secureHash);

        return p;
    }

    private Map<String, String> parseRefundJsonResponse(String response) {
        Map<String, String> map = new HashMap<>();

        try {
            JsonObject jsonObject = new Gson().fromJson(response, JsonObject.class);
            for (Map.Entry<String, JsonElement> entry : jsonObject.entrySet()) {
                map.put(entry.getKey(), entry.getValue().getAsString());
            }
        } catch (JsonSyntaxException | JsonIOException e) {
            return new HashMap<>();
        }

        return map;
    }

    public Optional<VnpayRefundTransaction> finalizeRefundTransaction(String jsonParams) {
        Map<String, String> params = parseRefundJsonResponse(jsonParams);
        if (params.isEmpty()) {
            return Optional.empty();
        }

        Optional<VnpayRefundTransaction> transactionOptional = refundRepository.findByTxnRef(params.get("vnp_TxnRef"));
        if (transactionOptional.isEmpty()) {
            return Optional.empty();
        }

        VnpayRefundTransaction transaction = transactionOptional.get();
        if (transaction.getDone()) {
            throw new FinalizedTransactionException();
        }

        transaction.setResponseId(params.get("vnp_ResponseId"));
        transaction.setResponseCode(params.get("vnp_ResponseCode"));
        transaction.setMessage(params.get("vnp_Message"));
        transaction.setBankCode(params.get("vnp_BankCode"));
        transaction.setPayDate(params.get("vnp_PayDate"));
        transaction.setTransactionNo(params.get("vnp_TransactionNo"));
        transaction.setTransactionType(params.get("vnp_TransactionType"));
        transaction.setTransactionStatus(params.get("vnp_TransactionStatus"));
        transaction.setDone(true);

        return Optional.of(refundRepository.save(transaction));
    }

    private Map<String, String> getPayParamMap(VnpayPayTransaction transaction) {
        Map<String, String> p = new HashMap<>();

        p.put("vnp_Version", transaction.getVersion());
        p.put("vnp_Command", transaction.getCommand());
        p.put("vnp_TmnCode", transaction.getTmnCode());
        p.put("vnp_Amount", String.valueOf(transaction.getAmount()));
        p.put("vnp_CreateDate", transaction.getCreateDate());
        p.put("vnp_CurrCode", transaction.getCurrCode());
        p.put("vnp_IpAddr", transaction.getIpAddr());
        p.put("vnp_Locale", transaction.getLocale());
        p.put("vnp_OrderInfo", transaction.getOrderInfo());
        p.put("vnp_OrderType", transaction.getOrderType());
        p.put("vnp_ReturnUrl", transaction.getReturnUrl());
        p.put("vnp_ExpireDate", transaction.getExpireDate());
        p.put("vnp_TxnRef", transaction.getTxnRef());

        return p;
    }
}
