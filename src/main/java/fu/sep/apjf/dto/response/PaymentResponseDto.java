package fu.sep.apjf.dto.response;

public record PaymentResponseDto(
    String paymentUrl,
    String vnpTxnRef
) {}
