package fu.sep.apjf.dto.response;

import java.util.List;

public record UserStatsResponseDto(
    int totalUser,
    List<UserMonthResponseDto> userMonth
) {}
