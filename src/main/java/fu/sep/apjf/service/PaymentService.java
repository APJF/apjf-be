package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.PaymentRequestDto;
import fu.sep.apjf.dto.response.PaymentResponseDto;
import fu.sep.apjf.entity.Payment;
import fu.sep.apjf.entity.PaymentStatus;
import fu.sep.apjf.entity.PaymentType;
import fu.sep.apjf.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService {
    private final PaymentRepository paymentRepository;
    // Inject other needed services: CourseService, UserService, etc.

    @Value("${vnpay.payUrl}")
    private String vnpayPayUrl;
    @Value("${vnpay.tmnCode}")
    private String vnpayTmnCode;
    @Value("${vnpay.hashSecret}")
    private String vnpayHashSecret;
    @Value("${vnpay.returnUrlDefault}")
    private String vnpayReturnUrl;
    @Value("${vnpay.currCode}")
    private String vnpayCurrCode;
    @Value("${vnpay.locale}")
    private String vnpayLocale;
    @Value("${vnpay.version}")
    private String vnpayVersion;
    @Value("${vnpay.expireMinutes}")
    private int vnpayExpireMinutes;

    private String buildVnpayUrl(String vnpTxnRef, Long amount, String orderInfo) {
        Map<String, String> params = new LinkedHashMap<>();
        params.put("vnp_Version", vnpayVersion);
        params.put("vnp_Command", "pay");
        params.put("vnp_TmnCode", vnpayTmnCode);
        params.put("vnp_Amount", String.valueOf(amount * 100)); // VNPay expects amount * 100
        params.put("vnp_CurrCode", vnpayCurrCode);
        params.put("vnp_TxnRef", vnpTxnRef);
        params.put("vnp_OrderInfo", orderInfo);
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale", vnpayLocale);
        params.put("vnp_ReturnUrl", vnpayReturnUrl);
        params.put("vnp_IpAddr", "0.0.0.0"); // Should be real client IP
        params.put("vnp_CreateDate", java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now()));
        params.put("vnp_ExpireDate", java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss").format(java.time.LocalDateTime.now().plusMinutes(vnpayExpireMinutes)));

        // Build hash data
        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(entry.getKey()).append('=').append(entry.getValue());
            if (query.length() > 0) query.append('&');
            query.append(URLEncoder.encode(entry.getKey(), StandardCharsets.UTF_8)).append('=')
                 .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        String secureHash = hmacSHA512(vnpayHashSecret, hashData.toString());
        query.append("&vnp_SecureHash=").append(secureHash);
        return vnpayPayUrl + "?" + query;
    }

    private String hmacSHA512(String key, String data) {
        try {
            javax.crypto.Mac hmac512 = javax.crypto.Mac.getInstance("HmacSHA512");
            javax.crypto.spec.SecretKeySpec secretKey = new javax.crypto.spec.SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512");
            hmac512.init(secretKey);
            byte[] bytes = hmac512.doFinal(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hash = new StringBuilder();
            for (byte b : bytes) {
                hash.append(String.format("%02x", b));
            }
            return hash.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error while hashing for VNPay", e);
        }
    }

    public PaymentResponseDto createPayment(Long userId, PaymentRequestDto dto) {
        String vnpTxnRef = UUID.randomUUID().toString().replace("-", "");
        String orderInfo = dto.type() == PaymentType.COURSE ? "Mua khoá học: " + dto.courseId() : "Nâng cấp Premium";
        String paymentUrl = buildVnpayUrl(vnpTxnRef, dto.amount(), orderInfo);
        Payment payment = Payment.builder()
                .userId(userId)
                .courseId(dto.type() == PaymentType.COURSE ? dto.courseId() : null)
                .type(dto.type())
                .status(PaymentStatus.PENDING)
                .amount(dto.amount())
                .vnpTxnRef(vnpTxnRef)
                .createdAt(LocalDateTime.now())
                .build();
        paymentRepository.save(payment);
        return new PaymentResponseDto(paymentUrl, vnpTxnRef);
    }

    public boolean verifyVnpayChecksum(Map<String, String> params) {
        String receivedHash = params.get("vnp_SecureHash");
        Map<String, String> sorted = new TreeMap<>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!entry.getKey().equals("vnp_SecureHash") && !entry.getKey().equals("vnp_SecureHashType")) {
                sorted.put(entry.getKey(), entry.getValue());
            }
        }
        StringBuilder hashData = new StringBuilder();
        for (Map.Entry<String, String> entry : sorted.entrySet()) {
            if (hashData.length() > 0) hashData.append('&');
            hashData.append(entry.getKey()).append('=').append(entry.getValue());
        }
        String calculatedHash = hmacSHA512(vnpayHashSecret, hashData.toString());
        return calculatedHash.equalsIgnoreCase(receivedHash);
    }

    public void handleReturn(Map<String, String> params) {
        if (!verifyVnpayChecksum(params)) throw new RuntimeException("VNPay checksum invalid");
        String vnpTxnRef = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef).orElseThrow();
        payment.setVnpayResponse(params.toString());
        if ("00".equals(params.get("vnp_ResponseCode"))) {
            payment.setStatus(PaymentStatus.SUCCESS);
            // Business logic: enroll/upgrade
            applyBusinessLogic(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
    }

    public String handleIpn(Map<String, String> params) {
        if (!verifyVnpayChecksum(params)) return "INVALID CHECKSUM";
        String vnpTxnRef = params.get("vnp_TxnRef");
        Payment payment = paymentRepository.findByVnpTxnRef(vnpTxnRef).orElse(null);
        if (payment == null) return "ORDER NOT FOUND";
        if (payment.getStatus() == PaymentStatus.SUCCESS) return "ORDER ALREADY SUCCESS";
        payment.setVnpayResponse(params.toString());
        if ("00".equals(params.get("vnp_ResponseCode"))) {
            payment.setStatus(PaymentStatus.SUCCESS);
            applyBusinessLogic(payment);
        } else {
            payment.setStatus(PaymentStatus.FAILED);
        }
        payment.setUpdatedAt(LocalDateTime.now());
        paymentRepository.save(payment);
        return "OK";
    }

    private void applyBusinessLogic(Payment payment) {
        // TODO: Inject and use CourseService/UserService
        // if (payment.getType() == PaymentType.COURSE) { enroll user vào khoá học }
        // if (payment.getType() == PaymentType.PREMIUM) { nâng cấp user }
    }
}
