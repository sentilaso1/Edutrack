package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.WebUtils;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.service.interfaces.VnpayTransactionService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Controller
public class VnpayController {

    private final VnpayConfig vnpayConfig;
    private final VnpayTransactionService vnpayTransactionService;

    @Autowired
    public VnpayController(VnpayConfig vnpayConfig, VnpayTransactionService vnpayTransactionService) {
        this.vnpayConfig = vnpayConfig;
        this.vnpayTransactionService = vnpayTransactionService;
    }

    @GetMapping("/api/vnpay/pay")
    public void pay(HttpSession session, HttpServletRequest request, HttpServletResponse response) throws IOException {
        User user = (User) session.getAttribute("loggedInUser");
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        VnpayTransaction transaction = new VnpayTransaction(
                VnpayTransaction.COMMAND_PAY,
                10000L,
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
    public String paymentReturn(@RequestParam Map<String, String> params, Model model) {
        String secureHash = params.remove("vnp_SecureHash");
        Map<String, String> sortedParams = new TreeMap<>(params);

        String hashData = sortedParams.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining("&"));

        String computedHash = vnpayConfig.hmacSHA512(
                vnpayConfig.vnpHashSecret, hashData
        );

        if (computedHash.equals(secureHash)) {
            String responseCode = params.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                model.addAttribute("success", "Payment successful!");
            } else {
                model.addAttribute("error", "Payment failed with response code: " + responseCode);
            }
        } else {
            model.addAttribute("error", "Invalid secure hash.");
        }

        return "redirect:/404";
    }
}
