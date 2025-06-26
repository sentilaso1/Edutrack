package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.transactions.dto.CommonTransaction;
import com.example.edutrack.transactions.model.BankingQR;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.model.Withdrawal;
import com.example.edutrack.transactions.service.BankingQrService;
import com.example.edutrack.transactions.service.CommonTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import com.example.edutrack.transactions.service.WithdrawalService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.Optional;

@Controller
public class WalletController {
    public static final int RECENT_TRANSACTION_LIMIT = 10;
    public static final int HISTORY_SIZE = 30;

    private final WalletService walletService;
    private final CommonTransactionService commonTransactionService;
    private final BankingQrService bankingQrService;
    private final WithdrawalService withdrawalService;

    @Autowired
    public WalletController(WalletService walletService, CommonTransactionService commonTransactionService, BankingQrService bankingQrService, WithdrawalService withdrawalService) {
        this.walletService = walletService;
        this.commonTransactionService = commonTransactionService;
        this.bankingQrService = bankingQrService;
        this.withdrawalService = withdrawalService;
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
            wallet = Optional.of(walletService.save(user));
        }

        model.addAttribute("wallet", wallet.get());
        return "/wallet/recharge";
    }

    @GetMapping("/wallet/refund")
    public String showRefundPage() {
        return "/wallet/refund";
    }

    @GetMapping("/wallet/withdraw")
    public String showWithdrawPage(
            HttpSession session,
            Model model,
            @RequestParam(value = "sort", required = false, defaultValue = "desc") String sortOrder,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "minAmount", required = false) Double minAmount,
            @RequestParam(value = "maxAmount", required = false) Double maxAmount
    ) {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        Wallet wallet = walletService.findById(user.getId()).orElseGet(() -> walletService.save(user));

        Optional<BankingQR> qr = bankingQrService.findByUser(user);
        qr.ifPresent(bankingQR -> {
            String base64QrImage = Base64.getEncoder().encodeToString(bankingQR.getQrImage());
            model.addAttribute("qr", base64QrImage);
        });

        Sort sort = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(Sort.Direction.ASC, "updatedDate")
                : Sort.by(Sort.Direction.DESC, "updatedDate");
        Pageable pageable = PageRequest.of(0, 30, sort);

        Double minInDb = withdrawalService.getMinAmountByWallet(wallet);
        Double maxInDb = withdrawalService.getMaxAmountByWallet(wallet);

        minAmount = (minAmount != null) ? minAmount : (minInDb != null ? minInDb : 0.0);
        maxAmount = (maxAmount != null) ? maxAmount : (maxInDb != null ? maxInDb : Double.MAX_VALUE);

        Page<Withdrawal> withdrawalsPage;
        if (status != null && !status.isEmpty()) {
            try {
                Withdrawal.Status statusEnum = Withdrawal.Status.valueOf(status);
                withdrawalsPage = withdrawalService.findByWalletAndStatusAndAmountRange(wallet, statusEnum, minAmount, maxAmount, pageable);
            } catch (IllegalArgumentException e) {
                withdrawalsPage = withdrawalService.findByWalletAndAmountRange(wallet, minAmount, maxAmount, pageable);
            }
        } else {
            withdrawalsPage = withdrawalService.findByWalletAndAmountRange(wallet, minAmount, maxAmount, pageable);
        }
        status = (status == null) ? "" : status;

        model.addAttribute("wallet", wallet);
        model.addAttribute("withdrawals", withdrawalsPage.getContent());
        model.addAttribute("sort", sortOrder);
        model.addAttribute("status", status);

        if (minInDb != null) {
            model.addAttribute("minAmount", minAmount);
        }
        if (maxInDb != null) {
            model.addAttribute("maxAmount", maxAmount);
        }

        return "/wallet/withdraw";
    }

    @PostMapping("/wallet/withdraw")
    public String requestWithdrawal(
            HttpSession session,
            Model model,
            @RequestParam("amount") String amountStr,
            @RequestParam(value = "qrImage", required = false) MultipartFile qrImage
    ) {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            return "redirect:/login";
        }

        Double amount;
        try {
            amount = Double.parseDouble(amountStr);
            amount = (double) Math.round(amount);
        } catch (NumberFormatException e) {
            model.addAttribute("error", "Invalid withdrawal amount.");
            return "/wallet/withdraw";
        }

        if (amount < Withdrawal.MINIMUM_WITHDRAWAL) {
            model.addAttribute("error", "Withdrawal amount must be larger than " + Withdrawal.MINIMUM_WITHDRAWAL);
            return "/wallet/withdraw";
        }

        Optional<Wallet> walletOpt = walletService.findById(user.getId());
        if (walletOpt.isEmpty()) {
            walletOpt = Optional.of(walletService.save(user));
        }
        Wallet wallet = walletOpt.get();

        if (wallet.getBalance() < amount) {
            model.addAttribute("error", "Insufficient wallet balance");
            return "/wallet/withdraw";
        }

        BankingQR qr = null;
        Optional<BankingQR> qrOpt = bankingQrService.findByUser(user);

        if (qrOpt.isEmpty()) {
            if (qrImage == null || qrImage.isEmpty()) {
                model.addAttribute("error", "Banking QR image is required.");
                return "/wallet/withdraw";
            }

            try {
                bankingQrService.save(new BankingQR(qrImage, user));
            } catch (IOException e) {
                model.addAttribute("error", "Failed to process banking qr image.");
                model.addAttribute("details", e.getStackTrace());
                return "/wallet/withdraw";
            }
        }

        Withdrawal withdrawal = new Withdrawal(amount.longValue(), wallet);
        withdrawalService.save(withdrawal);

        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setOnHold(wallet.getOnHold() + amount);
        walletService.save(wallet);

        return "redirect:/wallet/withdraw?success=true";
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
