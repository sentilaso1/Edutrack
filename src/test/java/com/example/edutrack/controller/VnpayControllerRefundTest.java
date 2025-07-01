package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.controller.VnpayController;

import com.example.edutrack.transactions.model.VnpayPayTransaction;
import com.example.edutrack.transactions.model.VnpayRefundTransaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.VnpayTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VnpayControllerRefundTest {

    @Mock
    private VnpayTransactionService vnpayTransactionService;

    @Mock
    private WalletService walletService;

    @Spy
    private VnpayConfig vnpayConfig;

    @Mock
    private HttpSession session;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    private VnpayController controller;

    private User user;
    private Wallet wallet;
    private VnpayPayTransaction payTransaction;
    private VnpayRefundTransaction refundTransaction;
    private String txnRef;

    @BeforeEach
    void setup() {
        controller = new VnpayController(vnpayConfig, vnpayTransactionService, walletService);

        user = createTestUser();
        wallet = new Wallet(user);
        wallet.setBalance(0.0);
        wallet.setOnHold(0.0);

        vnpayConfig = new VnpayConfig();
        vnpayConfig.vnpTmnCode = "SANUUQSC";
        vnpayConfig.vnpHashSecret = "X42OGTN8JYTZF13J447VDHJCTPMUMZS6";
        vnpayConfig.vnpApiUrl = "https://sandbox.vnpayment.vn/paymentv2/vpcpay.html";
        vnpayConfig.vnpRefundUrl = "https://sandbox.vnpayment.vn/merchant_webapi/api/transaction";
        vnpayConfig.vnpReturnUrl = "http://localhost:6969/api/vnpay/return";

        txnRef = "TXN123";
        payTransaction = new VnpayPayTransaction();
        payTransaction.setVersion("2.1.0");
        payTransaction.setCommand(VnpayTransaction.COMMAND_PAY);
        payTransaction.setTmnCode("VNP_TMNCODE");
        payTransaction.setCurrCode("VND");
        payTransaction.setLocale("vn");
        payTransaction.setOrderType(VnpayTransaction.ORDER_TYPE_OTHER);
        payTransaction.setReturnUrl("http://localhost:6969/api/vnpay/return");
        payTransaction.setTxnRef("TXN123");
        payTransaction.setCreateDate("20240615093000");
        payTransaction.setExpireDate("20240615100000");
        payTransaction.setAmount(20000L);
        payTransaction.setBalance(20000.0);
        payTransaction.setTransactionNo("VNP123");
        payTransaction.setWallet(wallet);
        payTransaction.setIpAddr("127.0.0.1");
        payTransaction.setOrderInfo("Nap tien EduTrack tai khoan test@example.com. So tien 20000 VND");

        refundTransaction = new VnpayRefundTransaction();
        refundTransaction.setVersion("2.1.0");
        refundTransaction.setCommand(VnpayTransaction.COMMAND_REFUND);
        refundTransaction.setTmnCode("VNP_TMNCODE");
        refundTransaction.setRequestId("RFD123456789");
        refundTransaction.setTransactionType(VnpayRefundTransaction.TYPE_FULL_REFUND);
        refundTransaction.setCreateDate("20240615100000");
        refundTransaction.setTxnRef(payTransaction.getTxnRef());
        refundTransaction.setTransactionNo(payTransaction.getTransactionNo());
        refundTransaction.setTransactionDate(payTransaction.getCreateDate());
        refundTransaction.setCreateBy(user.getEmail());
        refundTransaction.setAmount(payTransaction.getAmount() / VnpayTransaction.FRACTION_SHIFT);
        refundTransaction.setWallet(wallet);
        refundTransaction.setIpAddr("127.0.0.1");
        refundTransaction.setOrderInfo("Hoan tien giao dich " + payTransaction.getTxnRef());
    }

    private User createTestUser() {
        User user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        user.setPassword("password123");
        user.setFullName("Test User");
        user.setPhone("1234567890");
        user.setGender(User.GENDER_MALE);
        user.setCreatedDate(new Date());
        user.setIsLocked(false);
        user.setIsActive(true);
        return user;
    }

    @Test
    void shouldRedirectToLoginIfUserNull() throws IOException {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(null);

        controller.refund(session, request, response, txnRef);

        verify(response).sendRedirect("/login");
    }

    @Test
    void shouldRedirectIfTransactionNotFound() throws IOException {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(vnpayTransactionService.findPayTransaction(txnRef)).thenReturn(Optional.empty());

        controller.refund(session, request, response, txnRef);

        verify(response).sendRedirect("/wallet/refund?error=No matching payment");
    }

    @Test
    void shouldRedirectIfAmountInvalid() throws IOException {
        payTransaction = spy(new VnpayPayTransaction());
        doReturn(0L).when(payTransaction).getAmount(); // Simulate invalid amount

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(vnpayTransactionService.findPayTransaction(txnRef)).thenReturn(Optional.of(payTransaction));

        controller.refund(session, request, response, txnRef);

        verify(response).sendRedirect("/wallet/refund?error=Invalid amount");
    }

    @Test
    void shouldRedirectIfInsufficientBalance() throws IOException {
        wallet.setBalance(10.0);

        payTransaction.setAmount(50000L);
        payTransaction.setBalance(50000.0);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(vnpayTransactionService.findPayTransaction(txnRef)).thenReturn(Optional.of(payTransaction));
        when(walletService.findByUser(user)).thenReturn(Optional.of(wallet));

        controller.refund(session, request, response, txnRef);

        verify(response).sendRedirect("/wallet/refund?error=Insufficient balance for refund");
    }

    @Test
    void shouldProcessRefundSuccessfully() throws Exception {
        controller = spy(new VnpayController(vnpayConfig, vnpayTransactionService, walletService));

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);

        wallet.setBalance(100000.0);
        when(walletService.findByUser(user)).thenReturn(Optional.of(wallet));

        payTransaction.setAmount(50000L);
        payTransaction.setBalance(50000.0);
        payTransaction.setTxnRef(txnRef);
        payTransaction.setCreateDate("20240615093000");
        payTransaction.setTransactionNo("TXN123");
        payTransaction.setWallet(wallet);
        when(vnpayTransactionService.findPayTransaction(txnRef)).thenReturn(Optional.of(payTransaction));
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");

        refundTransaction.setResponseCode("00");
        refundTransaction.setAmount(50000L);
        refundTransaction.setTxnRef(txnRef);
        refundTransaction.setTransactionNo("TXN123");
        refundTransaction.setCreateDate("20240615093000");
        refundTransaction.setWallet(wallet);
        refundTransaction.setCreateBy(user.getEmail());
        refundTransaction.setIpAddr("127.0.0.1");
        refundTransaction.setOrderInfo("Hoan tien EduTrack giao dich TXN123. So tien 50000 VND");

        when(vnpayTransactionService.createBaseRefundTransaction()).thenReturn(refundTransaction);
        when(vnpayTransactionService.save(refundTransaction)).thenReturn(refundTransaction);

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("mockKey", "mockValue");
        when(vnpayTransactionService.prepareRefundJson(refundTransaction)).thenReturn(jsonObject);
        when(vnpayTransactionService.finalizeRefundTransaction(anyString())).thenReturn(Optional.of(refundTransaction));

        URL mockUrl = mock(URL.class);
        HttpURLConnection mockConn = mock(HttpURLConnection.class);
        DataOutputStream mockOutput = mock(DataOutputStream.class);
        ByteArrayInputStream mockInput = new ByteArrayInputStream(
                "{\"vnp_TxnRef\":\"TXN123\",\"vnp_ResponseCode\":\"00\"}".getBytes()
        );

        when(mockUrl.openConnection()).thenReturn(mockConn);
        when(mockConn.getOutputStream()).thenReturn(mockOutput);
        when(mockConn.getInputStream()).thenReturn(mockInput);
        doReturn(mockUrl).when(controller).createRefundUrl(anyString());

        controller.refund(session, request, response, txnRef);

        verify(response).sendRedirect("/wallet/refund?success=true");
    }
}
