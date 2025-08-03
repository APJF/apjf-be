package fu.sep.apjf.utils;

import fu.sep.apjf.entity.User;
import fu.sep.apjf.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.Optional;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    // OAuth2 attributes constants
    private static final String EMAIL_ATTRIBUTE = "email";
    private static final String NAME_ATTRIBUTE = "name";
    private static final String PICTURE_ATTRIBUTE = "picture";
    // Default values
    private static final String DEFAULT_AVATAR_URL = "https://engineering.usask.ca/images/no_avatar.jpg";
    private static final String DEFAULT_USERNAME = "Google User";
    private final UserService userService;
    private final JwtUtils jwtUtils;
    @Value("${app.oauth2.redirectUri:http://localhost:5173/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
            String email = extractEmail(oAuth2User);

            log.info("OAuth2 authentication success for email: {}", email);

            User user = getOrCreateUser(oAuth2User);
            String redirectUrl = buildRedirectUrlWithTokens(user);

            log.info("Redirecting to: {}", redirectUrl);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);

        } catch (Exception e) {
            log.error("OAuth2 authentication failed: {}", e.getMessage(), e);
            handleAuthenticationError(response);
        }
    }

    private String extractEmail(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute(EMAIL_ATTRIBUTE);
        if (email == null || email.trim().isEmpty()) {
            log.error("Email not provided by OAuth2 provider for user: {}", oAuth2User.getName());
            // Ném OAuth2AuthenticationException thay vì IllegalArgumentException
            throw new OAuth2AuthenticationException("Email not provided by OAuth2 provider");
        }
        return email.toLowerCase().trim();
    }

    private User getOrCreateUser(OAuth2User oAuth2User) {
        String email = extractEmail(oAuth2User);
        String name = extractName(oAuth2User);
        String avatar = extractAvatar(oAuth2User);

        Optional<User> existingUser = userService.findByEmail(email);

        if (existingUser.isPresent()) {
            User user = existingUser.get();

            // Update OAuth2 info for existing user
            boolean needUpdate = false;

            // Update avatar if user doesn't have one or using default
            if (user.getAvatar() == null || user.getAvatar().equals(DEFAULT_AVATAR_URL)) {
                user.setAvatar(avatar);
                needUpdate = true;
            }

            // Ensure account is enabled (in case it was disabled before)
            if (!user.isEnabled()) {
                user.setEnabled(true);
                user.setEmailVerified(true);
                needUpdate = true;
                log.info("Enabling previously disabled account via OAuth2: {}", email);
            }

            // Update username if it's still default
            if ("new user".equals(user.getUsername())) {
                user.setUsername(name);
                needUpdate = true;
            }

            if (needUpdate) {
                return userService.save(user);
            }

            return user;
        } else {
            return userService.createOAuth2User(email, name, avatar);
        }
    }

    private String extractName(OAuth2User oAuth2User) {
        String name = oAuth2User.getAttribute(NAME_ATTRIBUTE);
        return name != null && !name.trim().isEmpty() ? name.trim() : DEFAULT_USERNAME;
    }

    private String extractAvatar(OAuth2User oAuth2User) {
        String picture = oAuth2User.getAttribute(PICTURE_ATTRIBUTE);
        return picture != null && !picture.trim().isEmpty() ? picture : DEFAULT_AVATAR_URL;
    }

    private String buildRedirectUrlWithTokens(User user) {
        String accessToken = jwtUtils.generateJwtToken(user, false);
        String refreshToken = jwtUtils.generateJwtToken(user, true);

        return UriComponentsBuilder.fromUriString(redirectUri.trim())
                .queryParam("token", accessToken)
                .queryParam("refreshToken", refreshToken)
                .queryParam(EMAIL_ATTRIBUTE, user.getEmail())
                .queryParam("username", user.getUsername())
                .build()
                .encode()
                .toUriString();
    }

    private void handleAuthenticationError(HttpServletResponse response) throws IOException {
        String loginUrl = redirectUri.replace("/oauth2/redirect", "/login");
        String errorUrl = UriComponentsBuilder.fromUriString(loginUrl)
                .queryParam("error", "oauth2_error")
                .queryParam("message", "Đăng nhập với Google thất bại")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(errorUrl);
    }
}
