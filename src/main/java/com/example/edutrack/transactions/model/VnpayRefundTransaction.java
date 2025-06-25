package com.example.edutrack.transactions.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "vnpay_refund_transactions")
public class VnpayRefundTransaction extends VnpayTransaction {
    public static final String RESPONSE_SUCCESS = "00";

    public static final String TYPE_FULL_REFUND = "02";
    public static final String TYPE_PARTIAL_REFUND = "03";

    @Column(name = "vnp_request_id", nullable = false, unique = true)
    private String requestId;

    @Column(name = "vnp_bank_code")
    private String bankCode;

    @Column(name = "vnp_transaction_type")
    private String transactionType;

    @Column(name = "vnp_transaction_date")
    private String transactionDate;

    @Column(name = "vnp_create_by")
    private String createBy;

    @Column(name = "vnp_response_id")
    private String responseId;

    @Column(name = "vnp_message")
    private String message;

    @Column(name = "vnp_pay_date")
    private String payDate;

    public VnpayRefundTransaction() {
    }

    public VnpayRefundTransaction(String requestId, String transactionType, String transactionDate, String createBy, String responseId, String message, String payDate) {
        this.requestId = requestId;
        this.transactionType = transactionType;
        this.transactionDate = transactionDate;
        this.createBy = createBy;
        this.responseId = responseId;
        this.message = message;
        this.payDate = payDate;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getCreateBy() {
        return createBy;
    }

    public void setCreateBy(String createBy) {
        this.createBy = createBy;
    }

    public String getResponseId() {
        return responseId;
    }

    public void setResponseId(String responseId) {
        this.responseId = responseId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDateRefund) {
        this.payDate = payDateRefund;
    }

    @Override
    public String toString() {
        return "VnpayRefundTransaction{" +
                "requestId='" + requestId + '\'' +
                ", transactionType='" + transactionType + '\'' +
                ", transactionDate='" + transactionDate + '\'' +
                ", createBy='" + createBy + '\'' +
                ", responseId='" + responseId + '\'' +
                ", message='" + message + '\'' +
                ", payDateRefund='" + payDate + '\'' +
                '}';
    }
}
