package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.transactions.controller.WalletController;
import com.example.edutrack.transactions.model.BankingQR;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.model.Withdrawal;
import com.example.edutrack.transactions.service.BankingQrService;
import com.example.edutrack.transactions.service.CommonTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import com.example.edutrack.transactions.service.WithdrawalService;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletControllerTest {

    @Mock
    private WalletService walletService;

    @Mock
    private BankingQrService bankingQrService;

    @Mock
    private WithdrawalService withdrawalService;

    @Mock
    private CommonTransactionService commonTransactionService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private WalletController controller;

    private User user;
    private Wallet wallet;
    private MockMultipartFile qrImage;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        controller = new WalletController(
                walletService,
                commonTransactionService,
                bankingQrService,
                withdrawalService
        );

        user = createTestUser();
        wallet = new Wallet(user);
        wallet.setBalance(0.0);
        wallet.setOnHold(0.0);
        qrImage = new MockMultipartFile("qrImage", "qr.png", "image/png", "dummy".getBytes());
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
    void shouldRedirectToLoginWhenUserIsNull() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(null);

        String result = controller.requestWithdrawal(session, model, "100", null);

        assertEquals("redirect:/login", result);
    }

    @Test
    void shouldReturnErrorForInvalidAmount() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);

        String result = controller.requestWithdrawal(session, model, "abc", null);

        assertEquals("/wallet/withdraw", result);
        verify(model).addAttribute(eq("error"), contains("Invalid withdrawal amount"));
    }

    @Test
    void shouldReturnErrorForAmountBelowMinimum() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);

        String result = controller.requestWithdrawal(session, model, "5", null);

        assertEquals("/wallet/withdraw", result);
        verify(model).addAttribute(eq("error"), contains("Withdrawal amount must be larger"));
    }

    @Test
    public void shouldReturnErrorIfBalanceIsInsufficient() {
        wallet.setBalance(100000.0);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(any())).thenReturn(Optional.of(wallet));

        String view = controller.requestWithdrawal(session, model, "200000", null);
        verify(model).addAttribute(eq("error"), eq("Insufficient wallet balance"));
        assertEquals("/wallet/withdraw", view);
    }

    @Test
    void shouldReturnErrorIfQrMissingAndNotSaved() {
        wallet.setBalance(100000.0);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));
        when(bankingQrService.findByUser(user)).thenReturn(Optional.empty());

        String result = controller.requestWithdrawal(session, model, "5000", null);

        assertEquals("/wallet/withdraw", result);
        verify(model).addAttribute(eq("error"), eq("Banking QR image is required."));
    }

    @Test
    void shouldProcessWithdrawalSuccessfully() throws IOException {
        wallet.setBalance(100000.0);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.empty());
        when(walletService.save(user)).thenReturn(wallet);
        when(walletService.save(wallet)).thenReturn(wallet);
        when(bankingQrService.findByUser(user)).thenReturn(Optional.empty());

        String result = controller.requestWithdrawal(session, model, "20000", qrImage);

        assertEquals("redirect:/wallet/withdraw?success=true", result);
        verify(withdrawalService).save(any(Withdrawal.class));
        verify(walletService).save(any(User.class));
        verify(walletService).save(any(Wallet.class));
    }

    @Test
    public void shouldReturnErrorOnIOExceptionWhenCreatingQr() throws IOException {
        wallet.setBalance(100000.0);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));
        when(bankingQrService.findByUser(user)).thenReturn(Optional.empty());

        doThrow(new IOException("QR creation failed"))
                .when(bankingQrService).save(any(BankingQR.class));
        String view = controller.requestWithdrawal(session, model, "5000", qrImage);

        verify(model).addAttribute(eq("error"), eq("Failed to process banking qr image."));
        assertEquals("/wallet/withdraw", view);
    }

    @Test
    public void shouldReturnErrorOnIOExceptionWhenUpdatingQr() throws IOException {
        wallet.setBalance(100000.0);
        BankingQR qr = new BankingQR(qrImage, user);

        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));
        when(bankingQrService.findByUser(user)).thenReturn(Optional.of(qr));

        doThrow(new IOException("QR update failure"))
                .when(bankingQrService).save(any(BankingQR.class));
        String view = controller.requestWithdrawal(session, model, "5000", qrImage);

        verify(model).addAttribute(eq("error"), eq("Failed to process banking qr image."));
        assertEquals("/wallet/withdraw", view);
    }
}
