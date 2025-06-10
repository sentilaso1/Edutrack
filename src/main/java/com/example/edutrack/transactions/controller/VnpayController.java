package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.common.model.WebUtils;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.interfaces.VnpayTransactionService;
import com.example.edutrack.transactions.service.interfaces.WalletService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class VnpayController {

    private final VnpayConfig vnpayConfig;
    private final VnpayTransactionService vnpayTransactionService;
    private final WalletService walletService;

    @Autowired
    public VnpayController(VnpayConfig vnpayConfig, VnpayTransactionService vnpayTransactionService, WalletService walletService) {
        this.vnpayConfig = vnpayConfig;
        this.vnpayTransactionService = vnpayTransactionService;
        this.walletService = walletService;
    }

    @PostMapping("/api/vnpay/pay")
    public void pay(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam("amount") Long amount) throws IOException {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        if (!VnpayTransaction.isValidAmount(amount)) {
            response.sendRedirect("/wallet/recharge?error=Invalid amount");
            return;
        }

        VnpayTransaction transaction = new VnpayTransaction(
                VnpayTransaction.COMMAND_PAY,
                amount,
                WebUtils.getRealClientIp(request),
                VnpayTransaction.ORDER_TYPE_OTHER,
                user
        );
        vnpayTransactionService.save(transaction);

        response.sendRedirect(
                vnpayTransactionService.prepareTransactionUrl(transaction, vnpayConfig)
        );
    }

    @GetMapping("/api/vnpay/return")
    public String paymentReturn(HttpServletRequest request, Model model) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements();) {
            String fieldName = URLEncoder.encode(params.nextElement(), StandardCharsets.US_ASCII);
            String fieldValue = URLEncoder.encode(request.getParameter(fieldName), StandardCharsets.US_ASCII);

            if (fieldValue != null && !fieldValue.isEmpty()) {
                fields.put(fieldName, fieldValue);
            }
        }

        String secureHash = request.getParameter("vnp_SecureHash");
        fields.remove("vnp_SecureHashType");
        fields.remove("vnp_SecureHash");
        String computedHash = vnpayConfig.hashAllFields(fields);

        Optional<VnpayTransaction> transactionOpt = vnpayTransactionService.finalizeTransaction(fields);
        if (transactionOpt.isEmpty()) {
            model.addAttribute(CommonModelAttribute.ERROR.toString(), "Transaction not found or already processed.");
            return "redirect:/404";
        }

        if (computedHash.equals(secureHash)) {
            String responseCode = fields.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                User user = transactionOpt.get().getUser();
                Long amount = transactionOpt.get().getAmount();

                walletService.addFunds(
                        user,
                        ((double) amount / VnpayTransaction.FRACTION_SHIFT)
                );

                model.addAttribute(CommonModelAttribute.ERROR.toString(), "Payment successful!");
            } else {
                model.addAttribute(CommonModelAttribute.ERROR.toString(), "Payment failed with response code: " + responseCode);
            }
        } else {
            model.addAttribute(CommonModelAttribute.ERROR.toString(), "Invalid secure hash.");
        }

        return "redirect:/wallet/recharge";
    }
}
