package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LoginRequestDto;
import fu.sep.apjf.dto.request.RegisterDto;
import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.dto.response.UserResponseDto;
import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.Token;
import fu.sep.apjf.entity.Token.TokenType;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.exception.AppException;
import fu.sep.apjf.exception.UnverifiedAccountException;
import fu.sep.apjf.mapper.UserMapper;
import fu.sep.apjf.repository.AuthorityRepository;
import fu.sep.apjf.repository.TokenRepository;
import fu.sep.apjf.repository.UserRepository;
import fu.sep.apjf.utils.EmailUtils;
import fu.sep.apjf.utils.JwtUtils;
import fu.sep.apjf.utils.OtpUtils;
import io.jsonwebtoken.Claims;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Primary
public class UserService {

    private static final Duration OTP_TTL = Duration.ofMinutes(10);
    private static final Duration OTP_THROTTLE = Duration.ofMinutes(1);
    private static final String ROLE_USER = "ROLE_USER";
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final OtpUtils otpUtils;
    private final EmailUtils emailUtils;
    private final JwtUtils jwtUtils;
    private final AuthorityRepository authorityRepository;
    private final MinioService minioService;
    private final UserMapper userMapper;

    @Value("${app.jwt.jwtExpirationMs:3600000}")
    private long jwtExpirationMs;

    private LoginResponseDto createLoginResponse(User user) {
        String accessToken = jwtUtils.generateJwtToken(user, false);
        String refreshToken = jwtUtils.generateJwtToken(user, true);

        return new LoginResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                jwtExpirationMs
        );
    }

    // Method để lấy profile với presigned avatar URL
    public UserResponseDto getProfile(String email) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        UserResponseDto userDto = userMapper.toDto(user);

        // Convert avatar object name thành presigned URL
        String avatarUrl = userDto.avatar();
        if (avatarUrl != null && !avatarUrl.trim().isEmpty() &&
                !avatarUrl.startsWith("http://") && !avatarUrl.startsWith("https://")) {
            try {
                avatarUrl = minioService.getAvatarUrl(avatarUrl);
            } catch (Exception e) {
                log.error("Failed to generate avatar presigned URL for user {}: {}", email, e.getMessage());
                // Giữ nguyên object name nếu có lỗi
            }
        }

        return new UserResponseDto(
                userDto.id(),
                userDto.email(),
                userDto.username(),
                userDto.phone(),
                avatarUrl,
                userDto.authorities()
        );
    }

    // Method mới để upload avatar với validation
    public String uploadAvatar(MultipartFile file, String email) throws Exception {
        // Upload file qua MinioService (đã có validation bên trong)
        String objectName = minioService.uploadAvatar(file, email);

        // Cập nhật avatar trong database
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
        user.setAvatar(objectName);
        userRepository.save(user);

        return objectName;
    }

    // Method mới để login
    public LoginResponseDto login(LoginRequestDto loginDTO) {
        // 1. Kiểm tra email có tồn tại không
        User user = userRepository.findByEmailIgnoreCase(loginDTO.email())
                .orElseThrow(() -> new BadCredentialsException("Email không tồn tại trong hệ thống"));

        // 2. Kiểm tra mật khẩu có khớp không
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác");
        }

        // 3. Kiểm tra tài khoản đã được kích hoạt chưa
        if (!user.isEnabled()) {
            throw new UnverifiedAccountException("Tài khoản chưa được xác thực.", user.getEmail());
        }

        // 4. Tạo và trả về LoginResponseDto
        return createLoginResponse(user);
    }

    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {
        // 1. Validate refresh token và lấy claims
        Claims claims = jwtUtils.validateJwtToken(refreshToken);

        // 2. Kiểm tra loại token
        String tokenType = claims.get("tokenType", String.class);
        if (!"refresh".equals(tokenType)) {
            throw new BadCredentialsException("Token không phải là refresh token");
        }

        // 3. Lấy thông tin user từ refresh token
        String email = claims.getSubject();
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new BadCredentialsException("Người dùng không tồn tại"));

        // 4. Kiểm tra tài khoản vẫn còn active
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Tài khoản đã bị vô hiệu hóa");
        }

        // 5. Tạo và trả về LoginResponseDto
        return createLoginResponse(user);
    }

    @Transactional
    public void register(RegisterDto registerDTO) {
        if (userRepository.existsByEmailIgnoreCase(registerDTO.email())) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }

        // Tìm ROLE_USER và xử lý nếu không tìm thấy
        Authority userRole = authorityRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new AppException("Không tìm thấy ROLE_USER trong hệ thống."));

        User user = new User();
        user.setUsername("new user");
        user.setAuthorities(new ArrayList<>(List.of(userRole)));
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setEmail(registerDTO.email().toLowerCase()); // Lưu email dạng lowercase
        user.setAvatar("https://engineering.usask.ca/images/no_avatar.jpg");
        user.setEmailVerified(false);
        user.setEnabled(false);

        userRepository.save(user);

        // Gọi sendOtp với TokenType thay vì String
        sendOtp(user.getEmail(), TokenType.REGISTRATION);
    }

    @Transactional
    public void verifyAccount(String email, String otp) {
        // Tìm user theo email chính hoặc pendingEmail
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(email);
        if (userOpt.isEmpty()) {
            userOpt = userRepository.findAll().stream()
                    .filter(u -> email.equalsIgnoreCase(u.getPendingEmail()))
                    .findFirst();
        }
        User user = userOpt.orElseThrow(() -> new AppException("Email không tồn tại."));

        // Tìm token mới nhất (bất kể type nào)
        Token token = tokenRepository
                .findTopByUserOrderByRequestedTimeDesc(user)
                .orElseThrow(() -> new AppException("OTP không tồn tại."));

        if (token.getExpirationTime().isBefore(LocalDateTime.now()) || !token.getTokenValue().equals(otp)) {
            throw new AppException("OTP sai hoặc đã hết hạn.");
        }

        // Xử lý dựa trên type của token
        if (token.getType() == TokenType.REGISTRATION) {
            // Nếu xác thực đổi email
            if (email.equalsIgnoreCase(user.getPendingEmail())) {
                user.setEmail(user.getPendingEmail());
                user.setPendingEmail(null);
            }
            user.setEmailVerified(true);
            user.setEnabled(true);
            userRepository.save(user);
        }
        // Với reset password, chỉ cần xóa token, không cần thay đổi user
        // Logic reset password đã được xử lý �� method resetPassword

        tokenRepository.delete(token);
    }

    @Transactional
    public void resetPassword(String email, String otp, String newPassword) {
        User user = findByEmail(email).orElseThrow(() -> new EntityNotFoundException("Email không tồn tại"));
        Token token = tokenRepository.findTopByUserAndTypeOrderByRequestedTimeDesc(user, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("Token không hợp lệ"));

        if (!otpUtils.validateOTP(token.getTokenValue(), otp)) {
            throw new IllegalArgumentException("OTP không chính xác");
        }

        if (token.getExpirationTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("OTP đã hết hạn");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);

        // Vô hiệu hóa tất cả các token reset password của user này
        tokenRepository.deleteAllByUserAndType(user, TokenType.RESET_PASSWORD);
    }

    @Transactional
    public void changePassword(String email, String oldPassword, String newPassword) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu cũ không chính xác");
        }

        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu mới không được trùng với mật khẩu cũ");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        // Xóa tất cả token sau khi đổi mật khẩu
        tokenRepository.deleteAllByUser(user);
    }

    @Transactional
    public String updateProfile(String currentEmail, fu.sep.apjf.dto.request.UserRequestDto dto) {
        User user = userRepository.findByEmailIgnoreCase(currentEmail)
                .orElseThrow(() -> new EntityNotFoundException("Không tìm thấy người dùng"));
        boolean needSave = false;
        // Đổi email
        if (dto.email() != null && !dto.email().equalsIgnoreCase(user.getEmail())) {
            if (userRepository.existsByEmailIgnoreCase(dto.email())) {
                throw new IllegalArgumentException("Email đã tồn tại.");
            }
            user.setPendingEmail(dto.email());
            // Gọi sendOtp với TokenType thay vì String
            sendOtp(user.getEmail(), TokenType.REGISTRATION);
            needSave = true;
        }
        // Đổi username
        if (dto.username() != null && !dto.username().equals(user.getUsername())) {
            user.setUsername(dto.username());
            needSave = true;
        }
        // Đổi phone
        if (dto.phone() != null && !dto.phone().equals(user.getPhone())) {
            user.setPhone(dto.phone());
            needSave = true;
        }
        // Đổi avatar
        if (dto.avatar() != null && !dto.avatar().equals(user.getAvatar())) {
            user.setAvatar(dto.avatar());
            needSave = true;
        }
        if (needSave) {
            userRepository.save(user);
        }
        if (dto.email() != null && !dto.email().equalsIgnoreCase(user.getEmail())) {
            return "Đã gửi OTP xác thực đến email mới. Vui lòng xác thực để hoàn tất đổi email.";
        }
        return "Cập nhật thông tin thành công.";
    }

    /**
     * Phương thức tạo và gửi OTP với nhiều mục đích khác nhau
     *
     * @param email     Email của người dùng
     * @param tokenType Loại OTP (TokenType.REGISTRATION, TokenType.RESET_PASSWORD)
     * @return Thông báo kết quả
     */
    @Transactional
    public String sendOtp(String email, TokenType tokenType) {
        User user = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new AppException("Email không tồn tại."));

        String message;

        switch (tokenType) {
            case REGISTRATION:
                // Kiểm tra xem có OTP cũ không
                Token lastToken = tokenRepository
                        .findTopByUserAndTypeOrderByRequestedTimeDesc(user, tokenType)
                        .orElse(null);

                if (lastToken != null) {
                    // Kiểm tra throttle nếu có OTP cũ
                    if (Duration.between(lastToken.getRequestedTime(), LocalDateTime.now()).compareTo(OTP_THROTTLE) < 0) {
                        throw new AppException("Vui lòng chờ ít nhất 1 phút trước khi yêu cầu gửi lại OTP.");
                    }
                    // Xóa token cũ
                    tokenRepository.delete(lastToken);
                }

                // Kiểm tra trạng thái tài khoản cho verification
                if (user.isEnabled()) {
                    message = "OTP đã được gửi lại thành công.";
                } else {
                    message = "Đã gửi mã OTP xác thực tài khoản vào email của bạn.";
                }
                break;

            case RESET_PASSWORD:
                message = "Đã gửi email xác thực đặt lại mật khẩu.";
                break;

            default:
                throw new IllegalArgumentException("Loại token không được hỗ trợ: " + tokenType);
        }

        // Xóa các token cũ của loại này
        tokenRepository.deleteAllByUserAndType(user, tokenType);

        // Tạo token mới
        LocalDateTime now = LocalDateTime.now();
        String otp = otpUtils.generateOTP();
        Token token = Token.builder()
                .user(user)
                .tokenValue(otp)
                .type(tokenType)
                .requestedTime(now)
                .expirationTime(now.plus(OTP_TTL))
                .build();

        tokenRepository.save(token);

        // Gửi email sử dụng EmailUtils.sendEmailType
        emailUtils.sendEmailType(user.getEmail(), otp, tokenType);

        return message;
    }


    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }


    public User save(User user) {
        if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            Authority defaultRole = authorityRepository.findByName(ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role mặc định ROLE_USER"));
            user.setAuthorities(List.of(defaultRole));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User createOAuth2User(String email, String name, String avatar) {
        // Lookup default role
        Authority userRole = authorityRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new AppException("Default role not found: ROLE_USER"));

        // Build new User
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setAvatar(avatar);
        newUser.setEnabled(true);
        newUser.setAuthorities(List.of(userRole));
        // Generate a random password and encode it
        String randomPwd = UUID.randomUUID().toString();
        newUser.setPassword(passwordEncoder.encode(randomPwd));

        // Persist and return
        return userRepository.save(newUser);
    }
}
