package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.repository.CommonTransactionRepository;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class WalletController {
    public static final int RECENT_TRANSACTION_LIMIT = 10;

    private final WalletService walletService;
    private final CommonTransactionRepository commonTransactionRepository;

    @Autowired
    public WalletController(WalletService walletService, CommonTransactionRepository commonTransactionRepository) {
        this.walletService = walletService;
        this.commonTransactionRepository = commonTransactionRepository;
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
                commonTransactionRepository.findAllByUserIdLimit(
                        wallet.get().getId(),
                        RECENT_TRANSACTION_LIMIT
                )
        );

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
}
