package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.PaymentType;

public record PaymentRequestDto(
    PaymentType type, // COURSE or PREMIUM
    String courseId, // required if type == COURSE
    Long amount // amount in VND
) {}

