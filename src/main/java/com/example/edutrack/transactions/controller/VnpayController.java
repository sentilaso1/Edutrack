package com.example.edutrack.transactions.controller;

import com.example.edutrack.accounts.model.User;
import com.example.edutrack.common.model.CommonModelAttribute;
import com.example.edutrack.common.model.WebUtils;
import com.example.edutrack.config.VnpayConfig;
import com.example.edutrack.transactions.model.VnpayPayTransaction;
import com.example.edutrack.transactions.model.VnpayRefundTransaction;
import com.example.edutrack.transactions.model.VnpayTransaction;
import com.example.edutrack.transactions.model.Wallet;
import com.example.edutrack.transactions.service.VnpayTransactionService;
import com.example.edutrack.transactions.service.WalletService;
import com.google.gson.JsonObject;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

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

        if (amount <= 0) {
            response.sendRedirect("/wallet/recharge?error=Invalid amount");
            return;
        }

        Optional<Wallet> walletOpt = walletService.findByUser(user);
        if (walletOpt.isEmpty()) {
            walletOpt = Optional.of(walletService.save(user));
        }
        Wallet wallet = walletOpt.get();

        VnpayPayTransaction transaction = vnpayTransactionService.createBasePayTransaction();
        transaction.setAmount(amount);
        transaction.setIpAddr(WebUtils.getRealClientIp(request));
        transaction.setWallet(wallet);
        transaction.setOrderInfo(String.format(
                "Nap tien EduTrack tai khoan %s. So tien %d %s",
                user.getEmail(),
                amount,
                transaction.getCurrCode()
        ));
        vnpayTransactionService.save(transaction);

        response.sendRedirect(
                vnpayTransactionService.preparePayUrl(transaction)
        );
    }

    @PostMapping("/api/vnpay/refund")
    public void refund(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam("txnRef") String txnRef) throws IOException {
        User user = (User) session.getAttribute(CommonModelAttribute.LOGGED_IN_USER.toString());
        if (user == null) {
            response.sendRedirect("/login");
            return;
        }

        Optional<VnpayPayTransaction> payTransactionOpt = vnpayTransactionService.findPayTransaction(txnRef);
        if (payTransactionOpt.isEmpty()) {
            response.sendRedirect("/wallet/refund?error=No matching payment");
            return;
        }
        VnpayPayTransaction payTransaction = payTransactionOpt.get();

        if (payTransaction.getAmount() <= 0) {
            response.sendRedirect("/wallet/refund?error=Invalid amount");
            return;
        }

        Optional<Wallet> walletOpt = walletService.findByUser(user);
        if (walletOpt.isEmpty()) {
            walletOpt = Optional.of(walletService.save(user));
        }
        Wallet wallet = walletOpt.get();

        if (wallet.getBalance() < payTransaction.getBalance() / VnpayTransaction.FRACTION_SHIFT) {
            response.sendRedirect("/wallet/refund?error=Insufficient balance for refund");
            return;
        }

        VnpayRefundTransaction transaction = vnpayTransactionService.createBaseRefundTransaction();
        transaction.setAmount(payTransaction.getAmount() / VnpayTransaction.FRACTION_SHIFT);
        transaction.setIpAddr(WebUtils.getRealClientIp(request));
        transaction.setWallet(wallet);
        transaction.setCreateBy(user.getEmail());
        transaction.setTransactionDate(payTransaction.getCreateDate());
        transaction.setTxnRef(payTransaction.getTxnRef());
        transaction.setTransactionNo(payTransaction.getTransactionNo());
        transaction.setOrderInfo(String.format(
                "Hoan tien EduTrack giao dich %s. So tien %d VND",
                payTransaction.getTxnRef(),
                payTransaction.getAmount() / VnpayTransaction.FRACTION_SHIFT
        ));

        vnpayTransactionService.save(transaction);
        JsonObject json = vnpayTransactionService.prepareRefundJson(transaction);

        URL url = createRefundUrl(vnpayConfig.vnpRefundUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setDoOutput(true);

        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.writeBytes(json.toString());
        wr.flush();
        wr.close();

        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String output;
        StringBuffer responseBuffer = new StringBuffer();
        while ((output = br.readLine()) != null) {
            responseBuffer.append(output);
        }
        br.close();

        Optional<VnpayRefundTransaction> refundTransactionOpt = vnpayTransactionService.finalizeRefundTransaction(responseBuffer.toString());
        if (refundTransactionOpt.isEmpty()) {
            response.sendRedirect("/wallet/refund?error=Refund process error");
            return;
        }
        VnpayRefundTransaction refundTransaction = refundTransactionOpt.get();

        if (!refundTransaction.getResponseCode().equals(VnpayRefundTransaction.RESPONSE_SUCCESS)) {
            response.sendRedirect("/wallet/refund?error=VNPAY refund process error");
            return;
        }

        wallet.setBalance(wallet.getBalance() - (refundTransaction.getAmount() / VnpayRefundTransaction.FRACTION_SHIFT));
        walletService.save(wallet);
        refundTransaction.setBalance(wallet.getBalance());
        vnpayTransactionService.save(refundTransaction);

        response.sendRedirect("/wallet/refund?success=true");
    }

    public URL createRefundUrl(String url) throws IOException {
        return new URL(url);
    }

    @GetMapping("/api/vnpay/return")
    public String paymentReturn(HttpServletRequest request, Model model) {
        Map<String, String> fields = new HashMap<>();
        for (Enumeration<String> params = request.getParameterNames(); params.hasMoreElements(); ) {
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

        Optional<VnpayPayTransaction> transactionOpt = vnpayTransactionService.finalizePayTransaction(fields);
        if (transactionOpt.isEmpty()) {
            model.addAttribute(CommonModelAttribute.ERROR.toString(), "Transaction not found or already processed.");
            return "redirect:/404";
        }

        if (computedHash.equals(secureHash)) {
            String responseCode = fields.get("vnp_ResponseCode");
            if ("00".equals(responseCode)) {
                User user = transactionOpt.get().getWallet().getUser();
                Long amount = transactionOpt.get().getAmount();

                Optional<Wallet> wallet = walletService.addFunds(
                        user,
                        ((double) amount / VnpayTransaction.FRACTION_SHIFT)
                );

                if (wallet.isEmpty()) {
                    walletService.save(user);
                    wallet = walletService.addFunds(
                            user,
                            ((double) amount / VnpayTransaction.FRACTION_SHIFT)
                    );
                }

                transactionOpt.get().setBalance(wallet.get().getBalance());
                vnpayTransactionService.save(transactionOpt.get());

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
