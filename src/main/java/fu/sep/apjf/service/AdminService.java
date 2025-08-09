package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UpdateUserAuthoritiesDto;
import fu.sep.apjf.dto.request.UpdateUserStatusDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.repository.AuthorityRepository;
import fu.sep.apjf.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

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
        log.info("Admin {} cập nhật quyền cho user {}", adminEmail, dto.getUserId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + dto.getUserId()));

        // Kiểm tra không được cập nhật quyền của admin
        boolean isUserAdmin = user.getAuthorities().stream()
                .map(Authority.class::cast)
                .anyMatch(authority -> ROLE_ADMIN.equals(authority.getName()));

        if (isUserAdmin) {
            throw new AccessDeniedException("Không thể cập nhật quyền của admin");
        }

        // Lấy danh sách authorities theo IDs (chỉ cho phép non-admin roles)
        List<Authority> newAuthorities = authorityRepository.findAllById(dto.getAuthorityIds())
                .stream()
                .filter(authority -> !ROLE_ADMIN.equals(authority.getName()))
                .toList();

        if (newAuthorities.size() != dto.getAuthorityIds().size()) {
            throw new IllegalArgumentException("Một số authority ID không hợp lệ hoặc là ROLE_ADMIN");
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
        log.info("Admin {} {} user {}", adminEmail, dto.getEnabled() ? "unban" : "ban", dto.getUserId());

        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new EntityNotFoundException(USER_NOT_FOUND_MESSAGE + dto.getUserId()));

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

        user.setEnabled(dto.getEnabled());
        userRepository.save(user);

        String action = dto.getEnabled() ? "unban" : "ban";
        String reason = dto.getReason() != null ? " - Lý do: " + dto.getReason() : "";

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
}
