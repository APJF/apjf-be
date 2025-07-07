package fu.sep.apjf.service;

import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Custom OAuth2 User Service implementation for Google OAuth2 authentication.
 * Handles user registration and authentication via Google OAuth2.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final AuthorityRepository authorityRepository;

    private static final String GOOGLE_EMAIL_ATTRIBUTE = "email";
    private static final String GOOGLE_NAME_ATTRIBUTE = "name";
    private static final String GOOGLE_PICTURE_ATTRIBUTE = "picture";
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String DEFAULT_AVATAR_URL = "https://engineering.usask.ca/images/no_avatar.jpg";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        log.info("Bắt đầu xử lý đăng nhập OAuth2 với Google");

        try {
            // Load user info from Google
            OAuth2User oauth2User = loadOAuth2UserFromGoogle(userRequest);

            // Extract user attributes
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = extractEmail(attributes);
            String name = extractName(attributes);
            String picture = extractPicture(attributes);

            // Process user registration/login
            User user = processUserAuthentication(email, name, picture);

            // Create authorities for OAuth2User
            Collection<GrantedAuthority> authorities = createAuthorities(user);

            log.info("Đăng nhập OAuth2 thành công cho email: {}", email);
            return new DefaultOAuth2User(authorities, attributes, GOOGLE_NAME_ATTRIBUTE);

        } catch (Exception e) {
            log.error("Lỗi trong quá trình xử lý đăng nhập OAuth2: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("Không thể xử lý đăng nhập OAuth2: " + e.getMessage());
        }
    }

    /**
     * Load user information from Google OAuth2 provider
     */
    private OAuth2User loadOAuth2UserFromGoogle(OAuth2UserRequest userRequest) {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        return delegate.loadUser(userRequest);
    }

    /**
     * Extract and validate email from OAuth2 attributes
     */
    private String extractEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get(GOOGLE_EMAIL_ATTRIBUTE);
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email không được cung cấp từ Google");
        }
        return email.toLowerCase().trim();
    }

    /**
     * Extract name from OAuth2 attributes with fallback
     */
    private String extractName(Map<String, Object> attributes) {
        String name = (String) attributes.get(GOOGLE_NAME_ATTRIBUTE);
        return StringUtils.hasText(name) ? name.trim() : "Người dùng Google";
    }

    /**
     * Extract profile picture URL from OAuth2 attributes
     */
    private String extractPicture(Map<String, Object> attributes) {
        String picture = (String) attributes.get(GOOGLE_PICTURE_ATTRIBUTE);
        return StringUtils.hasText(picture) ? picture : DEFAULT_AVATAR_URL;
    }

    /**
     * Process user authentication - find existing user or create new one
     */
    private User processUserAuthentication(String email, String name, String picture) {
        Optional<User> existingUser = userService.findByEmail(email);

        if (existingUser.isPresent()) {
            log.info("Người dùng đã tồn tại, cập nhật thông tin đăng nhập cho email: {}", email);
            return updateExistingUser(existingUser.get(), name, picture);
        } else {
            log.info("Tạo người dùng mới từ Google OAuth2 cho email: {}", email);
            return createNewUser(email, name, picture);
        }
    }

    /**
     * Update existing user information if needed
     */
    private User updateExistingUser(User user, String name, String picture) {
        boolean needsUpdate = false;

        // Update name if different
        if (!name.equals(user.getUsername())) {
            user.setUsername(name);
            needsUpdate = true;
        }

        // Update avatar if different and not default
        if (!DEFAULT_AVATAR_URL.equals(picture) && !picture.equals(user.getAvatar())) {
            user.setAvatar(picture);
            needsUpdate = true;
        }

        // Ensure user is enabled
        if (!user.isEnabled()) {
            user.setEnabled(true);
            needsUpdate = true;
        }

        return needsUpdate ? userService.save(user) : user;
    }

    /**
     * Create new user from Google OAuth2 information
     */
    private User createNewUser(String email, String name, String picture) {
        Authority userRole = authorityRepository.findByAuthority(DEFAULT_ROLE)
                .orElseThrow(() -> new OAuth2AuthenticationException(
                        "Không tìm thấy role mặc định: " + DEFAULT_ROLE));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setAvatar(picture);
        newUser.setEnabled(true);
        newUser.setAuthorities(List.of(userRole));

        return userService.save(newUser);
    }

    /**
     * Create Spring Security authorities from user roles
     */
    private Collection<GrantedAuthority> createAuthorities(User user) {
        if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_ROLE));
        }

        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}