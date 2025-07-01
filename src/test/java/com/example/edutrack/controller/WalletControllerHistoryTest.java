package com.example.edutrack.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.transactions.controller.WalletController;
import com.example.edutrack.transactions.dto.CommonTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.BankingQrService;
import com.example.edutrack.transactions.service.CommonTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import com.example.edutrack.transactions.service.WithdrawalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import jakarta.servlet.http.HttpSession;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.ui.Model;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the WalletController's transaction history functionality (WalletController.java - showTransactionHistory method).
 * Test cases include: TC-2.1 to TC-2.7 (All passed)
 * <p>
 * Test case TC-2.1: Redirect to login if user is null.
 * Test case TC-2.2: Redirect to 404 if page is less than one.
 * Test case TC-2.3: Return error if wallet is not found.
 * Test case TC-2.4: List transactions in descending order without search.
 * Test case TC-2.5: List transactions in ascending order without search.
 * Test case TC-2.6: List transactions with search query.
 * Test case TC-2.7: List transactions if sort and search are null.
 */
@ExtendWith(MockitoExtension.class)
public class WalletControllerHistoryTest {
    @Mock
    private WalletService walletService;

    @Mock
    private CommonTransactionService commonTransactionService;

    @Mock
    private HttpSession session;

    @Mock
    private Model model;

    private WalletController walletController;

    private User user;
    private Wallet wallet;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        walletController = new WalletController(
                walletService,
                commonTransactionService,
                null,
                null
        );

        user = createTestUser();
        wallet = new Wallet(user);
        wallet.setBalance(0.0);
        wallet.setOnHold(0.0);
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
     * Test case TC-2.1: Redirect to login if user is null. (Invalid partition)
     */
    @Test
    void shouldRedirectToLoginIfUserIsNull() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(null);

        String view = walletController.showTransactionHistory(1, null, null, session, model);

        assertEquals("redirect:/login", view); // Unauthenticated
    }

    /**
     * Test case TC-2.2: Redirect to 404 if page is less than one. (Invalid boundary)
     */
    @Test
    void shouldRedirectTo404IfPageIsLessThanOne() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);

        String view = walletController.showTransactionHistory(0, null, null, session, model);

        assertEquals("redirect:/404", view); // Boundary failure
    }

    /**
     * Test case TC-2.3: Return error if wallet is not found. (Invalid partition)
     */
    @Test
    void shouldReturnErrorIfWalletNotFound() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.empty());

        String view = walletController.showTransactionHistory(1, null, null, session, model);

        assertEquals("/wallet/history", view);
        verify(model).addAttribute(eq("error"), eq("Missing wallet information"));
    }

    /**
     * Test case TC-2.4: List transactions in descending order without search. (Valid partition)
     */
    @Test
    void shouldListTransactionsDescendingWithoutSearch() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));

        Page<CommonTransaction> dummyPage = new PageImpl<>(List.of());
        when(commonTransactionService.findAllByUser(
                any(Pageable.class), any(String.class)
        )).thenReturn(dummyPage);

        String view = walletController.showTransactionHistory(1, "desc", null, session, model);

        assertEquals("/wallet/history", view);
        verify(model).addAttribute("page", dummyPage);
        verify(model).addAttribute("wallet", wallet);
        verify(model).addAttribute("pageNumber", 1);
        verify(model).addAttribute("sort", "desc");
    }

    /**
     * Test case TC-2.5: List transactions in ascending order without search. (Valid partition)
     */
    @Test
    void shouldListTransactionsAscendingWithoutSearch() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));

        Page<CommonTransaction> dummyPage = new PageImpl<>(List.of());
        when(commonTransactionService.findAllByUser(
                any(Pageable.class), any(String.class)
        )).thenReturn(dummyPage);

        String view = walletController.showTransactionHistory(1, "asc", null, session, model);

        assertEquals("/wallet/history", view);
        verify(model).addAttribute("sort", "asc");
    }

    /**
     * Test case TC-2.6: List transactions with search query. (Valid partition)
     */
    @Test
    void shouldListTransactionsWithSearchQuery() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));

        Page<CommonTransaction> dummyPage = new PageImpl<>(List.of());
        when(commonTransactionService.findAllByUserContaining(
                any(Pageable.class), any(String.class), any(String.class)
        )).thenReturn(dummyPage);

        String view = walletController.showTransactionHistory(1, "asc", "vnpay", session, model);
        assertEquals("/wallet/history", view);
        verify(model).addAttribute("search", "vnpay");
    }

    /**
     * Test case TC-2.7: List transactions if sort and search are null. (Valid boundary)
     */
    @Test
    void shouldListTransactionsIfSortAndSearchAreNull() {
        when(session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString())).thenReturn(user);
        when(walletService.findById(user.getId())).thenReturn(Optional.of(wallet));

        Page<CommonTransaction> dummyPage = new PageImpl<>(List.of());
        when(commonTransactionService.findAllByUser(
                any(Pageable.class), any(String.class)
        )).thenReturn(dummyPage);

        String view = walletController.showTransactionHistory(1, null, null, session, model);
        assertEquals("/wallet/history", view);
    }
}
