package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.UpdateUserAuthoritiesDto;
import fu.sep.apjf.dto.request.UpdateUserStatusDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.repository.*;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AdminServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AuthorityRepository authorityRepository;
    @Mock private UserMapper userMapper;
    @Mock private CourseRepository courseRepository;
    @Mock private ChapterRepository chapterRepository;
    @Mock private UnitRepository unitRepository;
    @Mock private MaterialRepository materialRepository;
    @Mock private ExamRepository examRepository;
    @Mock private CourseProgressRepository courseProgressRepository;

    @InjectMocks
    private AdminService adminService;

    private User mockUser;
    private UserResponseDto mockUserDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("user@example.com");
        mockUser.setEnabled(true);

        mockUserDto = new UserResponseDto(1L, "user@example.com", "Test User", null, null, false,false,null);
    }

    // ===========================================================
    // 1. Test getAllUsers()
    // ===========================================================
    @Test
    @DisplayName("Lấy danh sách users trừ admin hiện tại")
    void testGetAllUsers() {
        User admin = new User();
        admin.setEmail("admin@example.com");

        when(userRepository.findAll()).thenReturn(List.of(mockUser, admin));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        List<UserResponseDto> result = adminService.getAllUsers("admin@example.com");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).email()).isEqualTo("user@example.com");

        verify(userRepository).findAll();
        verify(userMapper).toDto(mockUser);
    }

    // ===========================================================
    // 2. Test getAllNonAdminAuthorities()
    // ===========================================================
    @Test
    @DisplayName("Lấy tất cả quyền trừ ROLE_ADMIN")
    void testGetAllNonAdminAuthorities() {
        Authority userRole = new Authority(1L,"ROLE_USER");
        Authority adminRole = new Authority(2L,"ROLE_ADMIN");

        when(authorityRepository.findAll()).thenReturn(List.of(userRole, adminRole));

        List<Authority> result = adminService.getAllNonAdminAuthorities();

        assertThat(result).containsExactly(userRole);
        verify(authorityRepository).findAll();
    }

    // ===========================================================
    // 3. Test updateUserAuthorities() thành công
    // ===========================================================
    @Test
    @DisplayName("Cập nhật quyền user thành công")
    void testUpdateUserAuthoritiesSuccess() {
        UpdateUserAuthoritiesDto dto = new UpdateUserAuthoritiesDto(1L, List.of(1L));

        Authority roleUser = new Authority(1L,"ROLE_USER");
        mockUser.setAuthorities(List.of());

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(authorityRepository.findAllById(dto.authorityIds())).thenReturn(List.of(roleUser));

        String result = adminService.updateUserAuthorities(dto, "admin@example.com");

        assertThat(result).contains("Cập nhật quyền thành công");
        verify(userRepository).save(mockUser);
    }

    // ===========================================================
    // 4. Test updateUserAuthorities() - không được cập nhật quyền admin
    // ===========================================================
    @Test
    @DisplayName("Không thể cập nhật quyền admin")
    void testUpdateUserAuthoritiesFail_Admin() {
        UpdateUserAuthoritiesDto dto = new UpdateUserAuthoritiesDto(1L, List.of(1L));

        Authority adminRole = new Authority(2L,"ROLE_ADMIN");
        mockUser.setAuthorities(List.of(adminRole));

        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> adminService.updateUserAuthorities(dto, "admin@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Không thể cập nhật quyền của admin");
    }

    // ===========================================================
    // 5. Test updateUserStatus() thành công
    // ===========================================================
    @Test
    @DisplayName("Ban user thành công")
    void testUpdateUserStatusSuccess() {
        UpdateUserStatusDto dto = new UpdateUserStatusDto(1L, false, "Spam");

        mockUser.setAuthorities(List.of(new Authority(1L,"ROLE_USER")));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        String result = adminService.updateUserStatus(dto, "admin@example.com");

        assertThat(result).contains("ban thành công");
        verify(userRepository).save(mockUser);
    }

    // ===========================================================
    // 6. Test updateUserStatus() - không thể ban admin
    // ===========================================================
    @Test
    @DisplayName("Không thể ban admin")
    void testUpdateUserStatusFail_Admin() {
        UpdateUserStatusDto dto = new UpdateUserStatusDto(1L, false, "Spam");

        Authority adminRole = new Authority(2L,"ROLE_ADMIN");
        mockUser.setAuthorities(List.of(adminRole));
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));

        assertThatThrownBy(() -> adminService.updateUserStatus(dto, "admin@example.com"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("Không thể ban/unban admin");
    }

    // ===========================================================
    // 7. Test getUserById()
    // ===========================================================
    @Test
    @DisplayName("Lấy thông tin user theo ID")
    void testGetUserById() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(mockUser));
        when(userMapper.toDto(mockUser)).thenReturn(mockUserDto);

        UserResponseDto result = adminService.getUserById(1L);

        assertThat(result).isEqualTo(mockUserDto);
        verify(userRepository).findById(1L);
        verify(userMapper).toDto(mockUser);
    }

    // ===========================================================
    // 8. Test getUserById() - user không tồn tại
    // ===========================================================
    @Test
    @DisplayName("Không tìm thấy user")
    void testGetUserByIdNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> adminService.getUserById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Không tìm thấy user với ID: 1");
    }
}
