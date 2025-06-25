package com.example.edutrack.transactions.model;

public enum VnpayResult {

    // Transaction Status
    TRANSACTION_SUCCESS("00"),
    TRANSACTION_PENDING("01"),
    TRANSACTION_ERROR("02"),
    TRANSACTION_REVERSED("04"),
    TRANSACTION_REFUND_PROCESSING("05"),
    TRANSACTION_REFUND_REQUESTED("06"),
    TRANSACTION_SUSPECTED_FRAUD("07"),
    TRANSACTION_REFUND_DECLINED("09"),

    // Response Code (IPN and Return URL)
    RESPONSE_SUCCESS("00"),
    RESPONSE_SUCCESS_BUT_SUSPECTED("07"),
    RESPONSE_INTERNETBANKING_NOT_REGISTERED("09"),
    RESPONSE_CARD_AUTH_FAILED_3_TIMES("10"),
    RESPONSE_PAYMENT_TIMEOUT("11"),
    RESPONSE_ACCOUNT_LOCKED("12"),
    RESPONSE_WRONG_OTP("13"),
    RESPONSE_CUSTOMER_CANCEL("24"),
    RESPONSE_NOT_ENOUGH_BALANCE("51"),
    RESPONSE_LIMIT_EXCEEDED("65"),
    RESPONSE_BANK_MAINTAINING("75"),
    RESPONSE_WRONG_PAYMENT_PASSWORD("79"),
    RESPONSE_OTHER_ERROR("99"),

    // Response Code (querydr)
    RESPONSE_QUERY_SUCCESS("00"),
    RESPONSE_QUERY_INVALID_TMN_CODE("02"),
    RESPONSE_QUERY_INVALID_FORMAT("03"),
    RESPONSE_QUERY_TRANSACTION_NOT_FOUND("91"),
    RESPONSE_QUERY_DUPLICATE_REQUEST("94"),
    RESPONSE_QUERY_CHECKSUM_INVALID("97"),

    // Response Code (refund)
    RESPONSE_REFUND_SUCCESS("00"),
    RESPONSE_REFUND_INVALID_TMN_CODE("02"),
    RESPONSE_REFUND_INVALID_FORMAT("03"),
    RESPONSE_REFUND_TRANSACTION_NOT_FOUND("91"),
    RESPONSE_REFUND_DUPLICATE_REQUEST("94"),
    RESPONSE_REFUND_TRANSACTION_FAILED("95"),
    RESPONSE_REFUND_CHECKSUM_INVALID("97");

    private final String code;

    VnpayResult(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public static VnpayResult fromCode(String code) {
        for (VnpayResult value : VnpayResult.values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return null;
    }
}
