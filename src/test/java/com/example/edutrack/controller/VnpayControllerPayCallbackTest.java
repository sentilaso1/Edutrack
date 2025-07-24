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
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ui.Model;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the VnpayController's payment return functionality (VnpayController.java - paymentReturn method).
 * Test cases include: TC-5.1 to TC-5.4 (All passed)
 * <p>
 * Test case TC-5.1: Redirect to 404 if transaction not found.
 * Test case TC-5.2: Redirect with success message if valid response.
 * Test case TC-5.3: Redirect with failure message if invalid secure hash.
 * Test case TC-5.4: Redirect with failure message if response code is not "00".
 */

class VnpayControllerPayCallbackTest {

    @Mock
    private VnpayTransactionService vnpayTransactionService;

    @Mock
    private WalletService walletService;

    @Mock
    private VnpayConfig vnpayConfig;

    @Mock
    private Model model;

    @Mock
    private HttpServletRequest request;

    private VnpayController controller;

    private User user;
    private Wallet wallet;
    private VnpayPayTransaction payTransaction;
    private VnpayRefundTransaction refundTransaction;
    private String txnRef;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        controller = new VnpayController(vnpayConfig, vnpayTransactionService, walletService);

        user = createTestUser();
        wallet = new Wallet(user);
        wallet.setBalance(0.0);
        wallet.setOnHold(0.0);

        txnRef = "TXN123";
        payTransaction = new VnpayPayTransaction();
        payTransaction.setVersion("2.1.0");
        payTransaction.setCommand(VnpayTransaction.COMMAND_PAY);
        payTransaction.setTmnCode("VNP_TMNCODE");
        payTransaction.setCurrCode("VND");
        payTransaction.setLocale("vn");
        payTransaction.setOrderType(VnpayTransaction.ORDER_TYPE_OTHER);
        payTransaction.setReturnUrl("http://localhost:6969/api/vnpay/return");
        payTransaction.setTxnRef(txnRef);
        payTransaction.setCreateDate("20240615093000");
        payTransaction.setExpireDate("20240615100000");
        payTransaction.setAmount(20000L);
        payTransaction.setBalance(20000.0);
        payTransaction.setTransactionNo("VNP123");
        payTransaction.setWallet(wallet);
        payTransaction.setIpAddr("127.0.0.1");
        payTransaction.setOrderInfo("Nap tien EduTrack tai khoan test@example.com. So tien 20000 VND");
    }

    /**
     * Helper method to create a test user.
     * This user will be used in the tests to simulate a logged-in user.
     */
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

    /**
     * Test case TC-5.1: Redirect to 404 if transaction not found. (Invalid partition)
     */
    @Test
    void shouldRedirectTo404IfTransactionNotFound() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", txnRef);

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenReturn("dummy");
        when(request.getParameter("vnp_SecureHash")).thenReturn("invalid-hash");
        when(vnpayTransactionService.finalizePayTransaction(any())).thenReturn(Optional.empty());

        String result = controller.paymentReturn(request, model);

        verify(model).addAttribute(eq(CommonModelAttribute.ERROR.toString()), anyString());
        assertEquals("redirect:/404", result);
    }

    /**
     * Test case TC-5.2: Redirect with success message if valid response. (Valid partition)
     */
    @Test
    void shouldRedirectWithSuccessMessageIfValidResponse() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "valid-hash");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(vnpayConfig.hashAllFields(anyMap())).thenReturn("valid-hash");
        when(vnpayTransactionService.finalizePayTransaction(any())).thenReturn(Optional.of(payTransaction));
        when(walletService.addFunds(eq(user), anyDouble())).thenReturn(Optional.of(wallet));

        String result = controller.paymentReturn(request, model);

        verify(model).addAttribute(CommonModelAttribute.ERROR.toString(), "Payment successful!");
        verify(vnpayTransactionService).save(payTransaction);
        assertEquals("redirect:/wallet/recharge", result);
    }

    /**
     * Test case TC-5.3: Redirect with failure message if invalid secure hash. (Invalid partition)
     */
    @Test
    void shouldRedirectWithFailureMessageIfInvalidHash() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_ResponseCode", "00");
        params.put("vnp_SecureHash", "invalid");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(vnpayConfig.hashAllFields(anyMap())).thenReturn("computed-valid-hash");
        when(vnpayTransactionService.finalizePayTransaction(any())).thenReturn(Optional.of(payTransaction));

        String result = controller.paymentReturn(request, model);

        verify(model).addAttribute(CommonModelAttribute.ERROR.toString(), "Invalid secure hash.");
        assertEquals("redirect:/wallet/recharge", result);
    }

    /**
     * Test case TC-5.4: Redirect with failure message if response code is not "00". (Valid partition)
     */
    @Test
    void shouldRedirectWithFailureMessageIfResponseCodeNot00() {
        Map<String, String> params = new HashMap<>();
        params.put("vnp_TxnRef", txnRef);
        params.put("vnp_ResponseCode", "24");
        params.put("vnp_SecureHash", "valid-hash");

        when(request.getParameterNames()).thenReturn(Collections.enumeration(params.keySet()));
        when(request.getParameter(anyString())).thenAnswer(invocation -> params.get(invocation.getArgument(0)));
        when(vnpayConfig.hashAllFields(anyMap())).thenReturn("valid-hash");
        when(vnpayTransactionService.finalizePayTransaction(any())).thenReturn(Optional.of(payTransaction));

        String result = controller.paymentReturn(request, model);

        verify(model).addAttribute(CommonModelAttribute.ERROR.toString(), "Payment failed with response code: 24");
        assertEquals("redirect:/wallet/recharge", result);
    }
}
