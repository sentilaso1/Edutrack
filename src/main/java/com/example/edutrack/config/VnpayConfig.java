package com.example.edutrack.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class VnpayConfig {

    @Value("${vnpay.tmnCode}")
    public String vnpTmnCode;

    @Value("${vnpay.hashSecret}")
    public String vnpHashSecret;

    @Value("${vnpay.apiUrl}")
    public String vnpApiUrl;

    @Value("${vnpay.refundUrl}")
    public String vnpRefundUrl;

    @Value("${vnpay.returnUrl}")
    public String vnpReturnUrl;

    public String hashAllFields(Map<String, String> params) {
        List<String> fieldNames = new ArrayList<>(params.keySet());
        Collections.sort(fieldNames);

        StringBuilder sb = new StringBuilder();
        Iterator<String> iter = fieldNames.iterator();

        while (iter.hasNext()) {
            String fieldName = iter.next();
            String fieldValue = params.get(fieldName);
            if (fieldValue != null && !fieldValue.isEmpty()) {
                sb.append(fieldName).append("=").append(fieldValue);

                if (iter.hasNext()) {
                    sb.append("&");
                }
            }
        }
        return hmacSHA512(vnpHashSecret, sb.toString());
    }

    public String hmacSHA512(String key, String data) {
        try {
            final Mac hmac512 = Mac.getInstance("HmacSHA512");
            byte[] hmacKeybytes = key.getBytes();
            SecretKeySpec secretKey = new SecretKeySpec(hmacKeybytes, "HmacSHA512");

            hmac512.init(secretKey);
            byte[] dataBytes = data.getBytes(StandardCharsets.UTF_8);
            byte[] result = hmac512.doFinal(dataBytes);

            StringBuilder sb = new StringBuilder(2 * result.length);
            for (byte b : result) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error initializing HMAC SHA512", e);
        }
    }
}
