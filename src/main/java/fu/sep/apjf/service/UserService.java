package fu.sep.apjf.service;

import fu.sep.apjf.dto.request.LoginRequestDto;
import fu.sep.apjf.dto.request.RegisterDto;
import fu.sep.apjf.dto.response.LoginResponseDto;
import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.Token;
import fu.sep.apjf.entity.Token.TokenType;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.exception.AppException;
import fu.sep.apjf.repository.AuthorityRepository;
import fu.sep.apjf.repository.TokenRepository;
import fu.sep.apjf.repository.UserRepository;
import fu.sep.apjf.utils.EmailUtils;
import fu.sep.apjf.utils.JwtUtils;
import fu.sep.apjf.utils.OtpUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository;
    @Lazy
    private final PasswordEncoder passwordEncoder;
    private final OtpUtils otpUtils;
    private final EmailUtils emailUtils;
    private final JwtUtils jwtUtils;
    private final AuthorityRepository authorityRepository;

    @Transactional
    public LoginResponseDto login(LoginRequestDto loginDTO) {
        // 1. Kiểm tra email có tồn tại không
        User user = userRepository.findByEmail(loginDTO.email())
                .orElseThrow(() -> new BadCredentialsException("Email không tồn tại trong hệ thống"));

        // 2. Kiểm tra mật khẩu có khớp không
        if (!passwordEncoder.matches(loginDTO.password(), user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác");
        }

        // 3. Kiểm tra tài khoản đã được kích hoạt chưa
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Tài khoản chưa được kích hoạt. Vui lòng kiểm tra email để kích hoạt tài khoản");
        }

        // 4. Lấy role & sinh cả access token và refresh token
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String accessToken = jwtUtils.generateTokenFromUsername(user);
        String refreshToken = jwtUtils.generateRefreshToken(user);

        // 5. Tạo đối tượng UserInfo
        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getAvatar(),
                roles

        );

        // 6. Trả về đối tượng LoginResponse với cả access token và refresh token
        return new LoginResponseDto(
                accessToken,
                refreshToken,
                "Bearer",
                userInfo
        );
    }

    @Transactional
    public LoginResponseDto refreshToken(String refreshToken) {
        // 1. Validate refresh token
        if (!jwtUtils.validateRefreshToken(refreshToken)) {
            throw new BadCredentialsException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // 2. Lấy thông tin user từ refresh token
        String email = jwtUtils.getEmailFromJwtToken(refreshToken);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new BadCredentialsException("Người dùng không tồn tại"));

        // 3. Kiểm tra tài khoản vẫn còn active
        if (!user.isEnabled()) {
            throw new BadCredentialsException("Tài khoản đã bị vô hiệu hóa");
        }

        // 4. Tạo cặp token mới
        List<String> roles = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        String newAccessToken = jwtUtils.generateTokenFromUsername(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(user);

        // 5. Tạo response
        LoginResponseDto.UserInfo userInfo = new LoginResponseDto.UserInfo(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getAvatar(),
                roles

        );

        return new LoginResponseDto(
                newAccessToken,     // access_token mới
                newRefreshToken,    // refresh_token mới
                "Bearer",           // token_type
                userInfo            // user object
        );
    }

    @Transactional
    public void register(RegisterDto registerDTO) {
        if (userRepository.existsByEmail(registerDTO.email())) {
            throw new IllegalArgumentException("Email đã tồn tại.");
        }
        Authority userRole = authorityRepository.findByAuthority("ROLE_USER")
                .orElseThrow();
        User user = new User();
        user.setUsername("new user");

        user.setAuthorities(new ArrayList<>(List.of(userRole)));
        user.setPassword(passwordEncoder.encode(registerDTO.password()));
        user.setEmail(registerDTO.email());
        user.setAvatar("https://engineering.usask.ca/images/no_avatar.jpg");
        user.setEnabled(false);
        userRepository.save(user);

        createAndSendToken(user, TokenType.REGISTRATION);
    }

    @Transactional
    public void verifyAccount(String email, String otp) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("Email không tồn tại."));

        // Tìm token mới nhất (bất kể type nào)
        Token token = tokenRepository
                .findTopByUserOrderByRequestedTimeDesc(user)
                .orElseThrow(() -> new AppException("OTP không tồn tại."));

        if (token.getExpirationTime().isBefore(LocalDateTime.now()) || !token.getTokenValue().equals(otp)) {
            throw new AppException("OTP sai hoặc đã hết hạn.");
        }

        // Xử lý dựa trên type của token
        if (token.getType() == TokenType.REGISTRATION) {
            user.setEnabled(true);
            userRepository.save(user);
        }
        // Với reset password, chỉ cần xóa token, không cần thay đổi user
        // Logic reset password đã được xử lý ở method resetPassword

        tokenRepository.delete(token);
    }

    @Transactional
    public void regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User không tồn tại."));
        Token token = tokenRepository
                .findTopByUserAndTypeOrderByRequestedTimeDesc(user, TokenType.REGISTRATION)
                .orElseThrow(() -> new AppException("Chưa có OTP trước đó."));

        if (Duration.between(token.getRequestedTime(), LocalDateTime.now()).compareTo(OTP_THROTTLE) < 0) {
            throw new AppException("Vui lòng chờ ít nhất 1 phút trước khi yêu cầu gửi lại OTP.");
        }

        tokenRepository.delete(token);
        createAndSendToken(user, TokenType.REGISTRATION);
    }

    @Transactional
    public void forgotPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AppException("User không tồn tại."));
        createAndSendToken(user, TokenType.RESET_PASSWORD);
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
        User user = findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Email không tồn tại"));

        // Kiểm tra mật khẩu cũ có đúng không
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadCredentialsException("Mật khẩu không chính xác");
        }

        // Đặt mật khẩu mới
        user.setPassword(passwordEncoder.encode(newPassword));
        save(user);
    }

    private void createAndSendToken(User user, TokenType type) {
        tokenRepository.deleteAllByUserAndType(user, type);
        LocalDateTime now = LocalDateTime.now();
        String otp = otpUtils.generateOTP();
        Token token = Token.builder()
                .user(user)
                .tokenValue(otp)
                .type(type)
                .requestedTime(now)
                .expirationTime(now.plus(OTP_TTL))
                .build();
        tokenRepository.save(token);
        emailUtils.sendEmailAsync(user.getEmail(), otp, type);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public User save(User user) {
        if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            Authority defaultRole = authorityRepository.findByAuthority("ROLE_USER")
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy role mặc định ROLE_USER"));
            user.setAuthorities(List.of(defaultRole));
        }
        return userRepository.save(user);
    }

    @Transactional
    public User createOAuth2User(String email, String name, String avatar) {
        // Lookup default role
        Authority userRole = authorityRepository.findByAuthority("ROLE_USER")
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
