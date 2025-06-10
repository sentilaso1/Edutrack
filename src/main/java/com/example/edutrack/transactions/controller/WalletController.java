package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Optional;

@Controller
public class WalletController {

    private final WalletService walletService;

    @Autowired
    public WalletController(WalletService walletService) {
        this.walletService = walletService;
    }

    @GetMapping("/wallet/recharge")
    public String showRechargePage(HttpSession session, Model model) {
        User user = (User) session.getAttribute("loggedInUser");
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
