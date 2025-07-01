package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.controller.VnpayController;
import com.example.edutrack.transactions.model.VnpayPayTransaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.VnpayTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class VnpayControllerPayTest {

    @Mock
    private VnpayTransactionService transactionService;

    @Mock
    private WalletService walletService;

    @Mock
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

    @BeforeEach
    void setUp() {
        controller = new VnpayController(vnpayConfig, transactionService, walletService);

        user = new User();
        user.setId(UUID.randomUUID());
        user.setEmail("test@example.com");
        wallet = new Wallet(user);
        wallet.setBalance(500000.0);
    }

    @Test
    void shouldRedirectToLoginIfUserNull() throws IOException {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(null);

        controller.pay(session, request, response, 50000L);

        verify(response).sendRedirect("/login");
    }

    @Test
    void shouldRedirectIfAmountInvalid() throws IOException {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);

        controller.pay(session, request, response, 0L);

        verify(response).sendRedirect("/wallet/recharge?error=Invalid amount");
    }

    @Test
    void shouldCreateWalletIfNotExist() throws IOException {
        VnpayPayTransaction transaction = new VnpayPayTransaction();
        transaction.setCurrCode("VND");
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findByUser(user)).thenReturn(Optional.empty());
        when(walletService.save(user)).thenReturn(wallet);
        when(transactionService.createBasePayTransaction()).thenReturn(transaction);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(transactionService.preparePayUrl(any(VnpayPayTransaction.class))).thenReturn("https://redirect.url");

        controller.pay(session, request, response, 50000L);

        verify(response).sendRedirect("https://redirect.url");
    }

    @Test
    void shouldProcessPaymentIfWalletExists() throws IOException {
        VnpayPayTransaction transaction = new VnpayPayTransaction();
        transaction.setCurrCode("VND");
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findByUser(user)).thenReturn(Optional.of(wallet));
        when(transactionService.createBasePayTransaction()).thenReturn(transaction);
        when(request.getRemoteAddr()).thenReturn("127.0.0.1");
        when(transactionService.preparePayUrl(any(VnpayPayTransaction.class))).thenReturn("https://redirect.url");

        controller.pay(session, request, response, 50000L);

        verify(transactionService).save(transaction);
        verify(response).sendRedirect("https://redirect.url");
    }
}
