package fu.sep.apjf.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

public record ApiResponseDto<T>(
        boolean success,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        long timestamp
) {
    public static <T> ApiResponseDto<T> ok(String msg, T data) {
        return new ApiResponseDto<>(true, msg, data, Instant.now().toEpochMilli());
    }

    public static ApiResponseDto<Object> error(String msg) {
        return new ApiResponseDto<>(false, msg, null, Instant.now().toEpochMilli());
    }

    public static <T> ApiResponseDto<T> error(String msg, T data) {
        return new ApiResponseDto<>(false, msg, data, Instant.now().toEpochMilli());
    }
}