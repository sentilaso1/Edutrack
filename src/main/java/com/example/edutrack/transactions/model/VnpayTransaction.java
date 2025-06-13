package com.example.edutrack.transactions.model;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CustomFormatter;
import jakarta.persistence.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

@Entity
@Table(name = "vnpay_transactions")
public class VnpayTransaction implements CommonTransaction {
    public static final String TIME_ZONE = "Etc/GMT+7";
    public static final String TIME_FORMAT = "yyyyMMddHHmmss";

    public static final int TRANSACTION_EXPIRATION_TIME = 15;  // minutes
    public static final int FRACTION_SHIFT = 100;
    public static final String ORDER_TYPE_OTHER = "other";

    public static final String COMMAND_PAY = "pay";
    public static final String COMMAND_QUERY = "querydr";
    public static final String COMMAND_REFUND = "refund";

    public static final long AMOUNT_100 = 100000L;
    public static final long AMOUNT_200 = 200000L;
    public static final long AMOUNT_500 = 500000L;
    public static final long AMOUNT_1000 = 1000000L;
    public static final long AMOUNT_2000 = 2000000L;
    public static final long AMOUNT_5000 = 5000000L;

    @Transient
    private final Calendar calendar = Calendar.getInstance(TimeZone.getTimeZone(TIME_ZONE));
    @Transient
    private final SimpleDateFormat dateFormat = new SimpleDateFormat(TIME_FORMAT);

    private String version = "2.1.0";
    private String locale = "vn";
    private String currCode = "VND";

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID txnRef;

    @Column(name = "command", nullable = false)
    private String command;

    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "created_date", nullable = false)
    private String createdDate = dateFormat.format(calendar.getTime());

    @Column(name = "expire_date", nullable = false)
    private String expireDate;

    @Column(name = "ip_address", nullable = false)
    private String ipAddress;

    @Column(name = "order_info", nullable = false)
    private String orderInfo;

    @Column(name = "order_type", nullable = false)
    private String orderType;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "is_done", nullable = false)
    private Boolean isDone = Boolean.FALSE;

    @Column(name = "bank_tran_no")
    private String bankTranNo;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "transaction_no")
    private String transactionNo;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "pay_date")
    private String payDate;

    @Column(name = "response_code")
    private String responseCode;

    @Column(name = "transaction_status")
    private String transactionStatus;

    public static boolean isValidAmount(Long amount) {
        amount /= FRACTION_SHIFT;
        return amount == AMOUNT_100 || amount == AMOUNT_200 ||
                amount == AMOUNT_500 || amount == AMOUNT_1000 ||
                amount == AMOUNT_2000 || amount == AMOUNT_5000;
    }

    public VnpayTransaction() {
    }

    public VnpayTransaction(String command, Long amount, String ipAddress, String orderType, User user) {
        this.command = command;
        this.amount = amount * FRACTION_SHIFT;
        this.ipAddress = ipAddress;
        this.orderType = orderType;
        this.user = user;
        this.orderInfo = String.format(
                "Nap tien EduTrack tai khoan %s. So tien %d %s",
                user.getEmail(),
                amount,
                this.currCode
        );

        calendar.add(Calendar.MINUTE, TRANSACTION_EXPIRATION_TIME);
        this.expireDate = dateFormat.format(calendar.getTime());
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getCurrCode() {
        return currCode;
    }

    public void setCurrCode(String currCode) {
        this.currCode = currCode;
    }

    public UUID getTxnRef() {
        return txnRef;
    }

    public void setTxnRef(UUID txnRef) {
        this.txnRef = txnRef;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        amount *= FRACTION_SHIFT;
        if (!isValidAmount(amount)) {
            throw new IllegalArgumentException("Invalid amount for VNPAY transaction");
        }
        this.amount = amount;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getExpireDate() {
        return expireDate;
    }

    public void setExpireDate(String expireDate) {
        this.expireDate = expireDate;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getOrderInfo() {
        return orderInfo;
    }

    public void setOrderInfo(String orderInfo) {
        this.orderInfo = orderInfo;
    }

    public String getOrderType() {
        return orderType;
    }

    public void setOrderType(String orderType) {
        this.orderType = orderType;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean isDone() {
        return isDone;
    }

    public void setDone(Boolean done) {
        isDone = done;
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

    public String getTransactionNo() {
        return transactionNo;
    }

    public void setTransactionNo(String transactionNo) {
        this.transactionNo = transactionNo;
    }

    public String getBankCode() {
        return bankCode;
    }

    public void setBankCode(String bankCode) {
        this.bankCode = bankCode;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getResponseCode() {
        return responseCode;
    }

    public void setResponseCode(String responseCode) {
        this.responseCode = responseCode;
    }

    @Override
    public UUID getTransactionId() {
        return getTxnRef();
    }

    @Override
    public String getTransactionInfo() {
        return getOrderInfo();
    }

    @Override
    public Double getTransactionAmount() {
        return (double) getAmount() / FRACTION_SHIFT;
    }

    @Override
    public String getTransactionAmountFormatted() {
        return CustomFormatter.formatNumberWithSpaces(getTransactionAmount(), true);
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    @Override
    public Date getTransactionDate() {
        Date date;
        try {
            if (payDate != null && !payDate.isEmpty()) {
                date = dateFormat.parse(payDate);
            } else {
                date = dateFormat.parse(createdDate);
            }
            return date;
        } catch (ParseException e) {
            throw new RuntimeException("Failed to parse transaction date", e);
        }
    }

    @Override
    public UUID getUserId() {
        return getUser().getId();
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    @Override
    public String toString() {
        return "VnpayTransaction{" +
                "txnRef=" + txnRef +
                ", amount=" + amount +
                ", createdDate='" + createdDate + '\'' +
                ", command='" + command + '\'' +
                ", expireDate='" + expireDate + '\'' +
                ", ipAddress='" + ipAddress + '\'' +
                ", orderInfo='" + orderInfo + '\'' +
                ", orderType='" + orderType + '\'' +
                ", user=" + user +
                ", isDone=" + isDone +
                ", bankTranNo='" + bankTranNo + '\'' +
                ", cardType='" + cardType + '\'' +
                ", transactionNo='" + transactionNo + '\'' +
                ", bankCode='" + bankCode + '\'' +
                ", payDate='" + payDate + '\'' +
                ", responseCode='" + responseCode + '\'' +
                ", transactionStatus='" + transactionStatus + '\'' +
                '}';
    }
}
