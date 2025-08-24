package fu.sep.apjf.dto.response;

import java.time.YearMonth;

public record UserMonthResponseDto(
        YearMonth month,
        long totalEnabledUsers

) {}