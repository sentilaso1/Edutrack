package com.example.edutrack.transactions.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "vnpay_pay_transactions")
public class VnpayPayTransaction extends VnpayTransaction {
    @Column(name = "vnp_bank_code")
    private String bankCode;

    @Column(name = "vnp_curr_code")
    private String currCode;

    @Column(name = "vnp_locale")
    private String locale;

    @Transient
    private String returnUrl;

    @Column(name = "vnp_expire_date")
    private String expireDate;

    @Column(name = "vnp_order_type")
    private String orderType;

    @Column(name = "vnp_bank_tran_no")
    private String bankTranNo;

    @Column(name = "vnp_card_type")
    private String cardType;

    @Column(name = "vnp_pay_date")
    private String payDate;

    public VnpayPayTransaction() {
    }

    public VnpayPayTransaction(String bankCode, String currCode, String locale, String returnUrl, String expireDate, String orderType, String bankTranNo, String cardType, String payDate) {
        this.bankCode = bankCode;
        this.currCode = currCode;
        this.locale = locale;
        this.returnUrl = returnUrl;
        this.expireDate = expireDate;
        this.orderType = orderType;
        this.bankTranNo = bankTranNo;
        this.cardType = cardType;
        this.payDate = payDate;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getReturnUrl() {
        return returnUrl;
    }

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public String getBankTranNo() {
        return bankTranNo;
    }

    public void setBankTranNo(String bankTranNo) {
        this.bankTranNo = bankTranNo;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    @Override
    public String toString() {
        return "VnpayPayTransaction{" +
                "bankCode='" + bankCode + '\'' +
                ", currCode='" + currCode + '\'' +
                ", locale='" + locale + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", orderType='" + orderType + '\'' +
                ", bankTranNo='" + bankTranNo + '\'' +
                ", cardType='" + cardType + '\'' +
                ", payDate='" + payDate + '\'' +
                '}';
    }
}
