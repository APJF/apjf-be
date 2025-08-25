package fu.sep.apjf.controller;

import fu.sep.apjf.dto.request.PaymentRequestDto;
import fu.sep.apjf.dto.response.ApiResponseDto;
import fu.sep.apjf.dto.response.PaymentResponseDto;
import fu.sep.apjf.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import fu.sep.apjf.entity.User;

import java.util.Map;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping("/course/{courseId}")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> createCoursePayment(@AuthenticationPrincipal User user,
                                                                                  @PathVariable String courseId,
                                                                                  @RequestBody PaymentRequestDto dto) {
        PaymentRequestDto req = new PaymentRequestDto(fu.sep.apjf.entity.PaymentType.COURSE, courseId, dto.amount());
        PaymentResponseDto response = paymentService.createPayment(user.getId(), req);
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo giao dịch mua khoá học thành công", response));
    }

    @PostMapping("/premium")
    public ResponseEntity<ApiResponseDto<PaymentResponseDto>> createPremiumPayment(@AuthenticationPrincipal User user,
                                                                                   @RequestBody PaymentRequestDto dto) {
        PaymentRequestDto req = new PaymentRequestDto(fu.sep.apjf.entity.PaymentType.PREMIUM, null, dto.amount());
        PaymentResponseDto response = paymentService.createPayment(user.getId(), req);
        return ResponseEntity.ok(ApiResponseDto.ok("Tạo giao dịch nâng cấp premium thành công", response));
    }

    @GetMapping("/vnpay/return")
    public ResponseEntity<String> handleVnpayReturn(@RequestParam Map<String, String> params) {
        paymentService.handleReturn(params);
        return ResponseEntity.ok("Thanh toán thành công. Vui lòng kiểm tra email hoặc lịch sử giao dịch.");
    }

    @PostMapping("/vnpay/ipn")
    public ResponseEntity<String> handleVnpayIpn(@RequestParam Map<String, String> params) {
        String result = paymentService.handleIpn(params);
        return ResponseEntity.ok(result);
    }
}
