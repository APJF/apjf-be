package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UpdateUserAuthoritiesDto;
import fu.sep.apjf.dto.request.UpdateUserStatusDto;
import fu.sep.apjf.dto.response.CourseProgressPercentResponseDto;
import fu.sep.apjf.dto.response.CourseTotalEnrollResponseDto;
import fu.sep.apjf.dto.response.DashboardManagerResponseDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.*;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AdminService {

    private static final String ROLE_ADMIN = "ROLE_ADMIN";
    private static final String USER_NOT_FOUND_MESSAGE = "Không tìm thấy user với ID: ";
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserMapper userMapper;
    private final CourseRepository courseRepository;
    private final ChapterRepository chapterRepository;
    private final UnitRepository unitRepository;
    private final MaterialRepository materialRepository;
    private final ExamRepository examRepository;
    private final CourseProgressRepository courseProgressRepository;

    /**
     * Lấy danh sách tất cả users (trừ admin hiện tại)
     */
    public List<UserResponseDto> getAllUsers(String adminEmail) {
        log.info("Admin {} lấy danh sách tất cả users", adminEmail);

        List<User> users = userRepository.findAll().stream()
                .filter(user -> !user.getEmail().equals(adminEmail))
                .toList();

        return users.stream()
                .map(userMapper::toDto)
                .toList();
    }

    /**
     * Lấy danh sách tất cả authorities (trừ ROLE_ADMIN)
     */
    public List<Authority> getAllNonAdminAuthorities() {
        log.info("Lấy danh sách tất cả quyền (trừ ROLE_ADMIN)");

        return authorityRepository.findAll().stream()
                .filter(authority -> !ROLE_ADMIN.equals(authority.getName()))
                .toList();
    }

    /**
     * Cập nhật quyền của user (không được cập nhật admin)
     */
    public String updateUserAuthorities(UpdateUserAuthoritiesDto dto, String adminEmail) {
        log.info("Admin {} cập nhật quyền cho user {}", adminEmail, dto.userId());

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + dto.userId()));

        // Kiểm tra không được cập nhật quyền của admin
        boolean isUserAdmin = user.getAuthorities().stream()
                .map(Authority.class::cast)
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getName()));

        if (isUserAdmin) {
            throw new AccessDeniedException("Không thể cập nhật quyền của admin");
        }

        // Lấy danh sách authorities theo IDs
        List<Authority> newAuthorities = authorityRepository.findAllById(dto.authorityIds());

        if (newAuthorities.size() != dto.authorityIds().size()) {
            throw new IllegalArgumentException("Một số authority ID không hợp lệ");
        }

        boolean containsAdminRole = newAuthorities.stream()
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getName()));
        if (containsAdminRole) {
            throw new AccessDeniedException("Không thể cấp quyền ROLE_ADMIN cho user");
        }

        user.setAuthorities(newAuthorities);
        userRepository.save(user);

        log.info("Cập nhật quyền thành công cho user {}", user.getEmail());
        return "Cập nhật quyền thành công cho user: " + user.getEmail();
    }

    /**
     * Ban/Unban user (không được ban admin)
     */
    public String updateUserStatus(UpdateUserStatusDto dto, String adminEmail) {
        log.info("Admin {} {} user {}", adminEmail, dto.enabled() ? "unban" : "ban", dto.userId());

        User user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + dto.userId()));

        // Kiểm tra không được ban admin
        boolean isUserAdmin = user.getAuthorities().stream()
                .map(Authority.class::cast)
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getName()));

        if (isUserAdmin) {
            throw new AccessDeniedException("Không thể ban/unban admin");
        }

        // Kiểm tra không tự ban chính mình
        if (user.getEmail().equals(adminEmail)) {
            throw new AccessDeniedException("Không thể tự ban chính mình");
        }

        user.setEnabled(dto.enabled());
        userRepository.save(user);

        String action = dto.enabled() ? "unban" : "ban";
        String reason = dto.reason() != null ? " - Lý do: " + dto.reason() : "";

        log.info("Thực hiện {} thành công cho user {}{}", action, user.getEmail(), reason);
        return "Thực hiện " + action + " thành công cho user: " + user.getEmail() + reason;
    }

    /**
     * Lấy thông tin chi tiết user theo ID
     */
    public UserResponseDto getUserById(Long userId) {
        log.info("Lấy thông tin user với ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        return userMapper.toDto(user);
    }

    public DashboardManagerResponseDto getDashboardData() {
        // 1. Tổng quan course, chapter, unit, material, exam
        int totalCourse = (int) courseRepository.count();
        int totalActiveCourse = courseRepository.countByStatus(EnumClass.Status.ACTIVE);
        int totalInactiveCourse = courseRepository.countByStatus(EnumClass.Status.INACTIVE);

        // 2. Đếm Chapter
        int totalChapter = (int) chapterRepository.count();
        int totalActiveChapter = chapterRepository.countByStatus(EnumClass.Status.ACTIVE);
        int totalInactiveChapter = chapterRepository.countByStatus(EnumClass.Status.INACTIVE);

        // 3. Đếm Unit
        int totalUnit = (int) unitRepository.count();
        int totalActiveUnit = unitRepository.countByStatus(EnumClass.Status.ACTIVE);
        int totalInactiveUnit = unitRepository.countByStatus(EnumClass.Status.INACTIVE);

        int totalMaterial = (int) materialRepository.count();
        int totalExam = (int) examRepository.count();

        // 2. Thống kê số học viên enrolled và completed theo tháng
        List<CourseTotalEnrollResponseDto> courseMonthlyActivity = getLast6MonthsStats();

        // 3. Thống kê % hoàn thành cho từng course
        List<CourseProgressPercentResponseDto> coursesTotalCompletedPercent = buildCourseProgressPercent();

        // 4. Trả về DashboardManagerResponseDto
        return new DashboardManagerResponseDto(
                totalCourse,
                totalActiveCourse,
                totalInactiveCourse,
                totalChapter,
                totalActiveChapter,
                totalInactiveChapter,
                totalUnit,
                totalActiveUnit,
                totalInactiveUnit,
                totalMaterial,
                totalExam,
                coursesTotalCompletedPercent,
                courseMonthlyActivity
        );
    }

    /**
     * Thống kê tổng số học viên đăng ký và hoàn thành khóa học theo tháng
     */
    public List<CourseTotalEnrollResponseDto> getLast6MonthsStats() {
        // Lấy thời điểm bắt đầu (6 tháng gần nhất)
        Instant startDate = YearMonth.now()
                .minusMonths(5)
                .atDay(1)
                .atStartOfDay(ZoneId.systemDefault())
                .toInstant();

        // Gọi repository và map sang DTO
        return courseProgressRepository.findLast6MonthsStats(startDate)
                .stream()
                .map(row -> new CourseTotalEnrollResponseDto(
                        YearMonth.from(((Timestamp) row[0]).toLocalDateTime()),
                        ((Number) row[1]).intValue(),
                        ((Number) row[2]).intValue()
                ))
                .toList();
    }

    /**
     * Thống kê % hoàn thành cho từng khóa học
     */
    private List<CourseProgressPercentResponseDto> buildCourseProgressPercent() {
        return courseProgressRepository.getCourseProgressPercent();
    }
}
