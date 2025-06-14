package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.transactions.model.CommonTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.interfaces.CommonTransactionService;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class WalletController {
    public static final int RECENT_TRANSACTION_LIMIT = 10;
    public static final int HISTORY_SIZE = 1;

    private final WalletService walletService;
    private final CommonTransactionService commonTransactionService;

    @Autowired
    public WalletController(WalletService walletService, CommonTransactionService commonTransactionService) {
        this.walletService = walletService;
        this.commonTransactionService = commonTransactionService;
    }

    @GetMapping("/wallet")
    public String myWallet(HttpSession session, Model model) {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Wallet> wallet = walletService.findById(user.getId());
        if (wallet.isEmpty()) {
            wallet = Optional.of(walletService.save(user));
        }

        model.addAttribute("wallet", wallet.get());
        model.addAttribute(
                "transactions",
                commonTransactionService.findAllByUser(
                        PageRequest.of(0, RECENT_TRANSACTION_LIMIT, Sort.by("date").descending()),
                        wallet.get().getId().toString()
                )
        );
        model.addAttribute("recentTransactions", commonTransactionService.findAllRecentTransactionsByUser(user.getId().toString()));

        return "/wallet/my-wallet";
    }

    @GetMapping("/wallet/recharge")
    public String showRechargePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        Optional<Wallet> wallet = walletService.findById(user.getId());
        if (wallet.isEmpty()) {
            model.addAttribute("error", "Missing wallet information");
            return "/wallet/recharge";
        }

        model.addAttribute("wallet", wallet.get());
        return "/wallet/recharge";
    }

    @GetMapping("/wallet/history")
    public String redirectShowTransactionHistory() {
        return "redirect:/wallet/history/1";
    }

    @GetMapping("/wallet/history/{page}")
    public String showTransactionHistory(
            @PathVariable("page") Integer page,
            @RequestParam(value = "sort", required = false) String sort,
            @RequestParam(value = "search", required = false) String search,
            HttpSession session,
            Model model
    ) {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        if (page - 1 < 0) {
            return "redirect:/404";
        }

        Optional<Wallet> wallet = walletService.findById(user.getId());
        if (wallet.isEmpty()) {
            model.addAttribute("error", "Missing wallet information");
            return "/wallet/history";
        }

        Sort pageRequestSort = (sort == null || sort.isEmpty() || sort.equals("desc")) ? Sort.by("date").descending() : Sort.by("date");
        if (sort != null) {
            model.addAttribute("sort", sort);
        }

        Page<CommonTransaction> transactionPage;
        if (search == null || search.isEmpty()) {
            transactionPage = commonTransactionService.findAllByUser(
                    PageRequest.of(page - 1, HISTORY_SIZE, pageRequestSort),
                    wallet.get().getId().toString()
            );
        } else {
            transactionPage = commonTransactionService.findAllByUserContaining(
                    PageRequest.of(page - 1, HISTORY_SIZE, pageRequestSort),
                    wallet.get().getId().toString(),
                    search
            );
            model.addAttribute("search", search);
        }

        model.addAttribute("wallet", wallet.get());
        model.addAttribute("pageNumber", page);
        model.addAttribute("page", transactionPage);

        return "/wallet/history";
    }
}
