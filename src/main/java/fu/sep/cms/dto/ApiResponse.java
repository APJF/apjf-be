package fu.sep.cms.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

public record ApiResponse<T>(
        boolean success,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL)
        T data,
        long timestamp
) {
    public static <T> ApiResponse<T> ok(String msg, T data) {
        return new ApiResponse<>(true, msg, data, Instant.now().toEpochMilli());
    }

    public static ApiResponse<?> error(String msg) {
        return new ApiResponse<>(false, msg, null, Instant.now().toEpochMilli());
    }
}