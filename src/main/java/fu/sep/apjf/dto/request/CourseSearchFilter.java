package fu.sep.apjf.dto.request;

import fu.sep.apjf.entity.EnumClass;

/**
 * DTO đại diện cho bộ lọc tìm kiếm khóa học
 * Được sử dụng để giảm số lượng tham số trong phương thức findAll của CourseService
 */
public record CourseSearchFilter(
        String title,
        EnumClass.Level level,
        EnumClass.Status status,
        Boolean entryOnly
) {
    public static CourseSearchFilter of(String title, EnumClass.Level level, EnumClass.Status status, Boolean entryOnly) {
        return new CourseSearchFilter(title, level, status, entryOnly);
    }
}
