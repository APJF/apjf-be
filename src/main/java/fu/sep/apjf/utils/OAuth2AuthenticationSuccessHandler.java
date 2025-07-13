package fu.sep.apjf.utils;

import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.repository.AuthorityRepository;
import fu.sep.apjf.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final UserService userService;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;
    private final AuthorityRepository authorityRepository;

    @Value("${app.oauth2.redirectUri:http://localhost:5173/oauth2/redirect}")
    private String redirectUri;

    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String DEFAULT_AVATAR_URL = "https://engineering.usask.ca/images/no_avatar.jpg";
    private static final String DEFAULT_USERNAME = "Google User";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        try {
            OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

            UserInfo userInfo = extractUserInfo(oAuth2User);

            User user = getOrCreateUser(userInfo);

            String jwt = generateJwtToken(user);
            String targetUrl = buildRedirectUrl(jwt, user);

            getRedirectStrategy().sendRedirect(request, response, targetUrl);
        } catch (Exception e) {
            log.error("OAuth2 authentication failed: {}", e.getMessage(), e);
            handleAuthenticationError(response, e);
        }
    }

    private UserInfo extractUserInfo(OAuth2User oAuth2User) {
        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        String picture = oAuth2User.getAttribute("picture");

        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("Email not provided by OAuth2 provider");
        }

        return new UserInfo(
                email.toLowerCase().trim(),
                name != null ? name.trim() : DEFAULT_USERNAME,
                picture != null ? picture : DEFAULT_AVATAR_URL
        );
    }

    private User getOrCreateUser(UserInfo userInfo) {
        Optional<User> userOptional = userService.findByEmail(userInfo.email());
        return userOptional.orElseGet(() -> createNewUser(userInfo));
    }

    private User createNewUser(UserInfo userInfo) {
        Authority userRole = authorityRepository.findByAuthority(DEFAULT_ROLE)
                .orElseThrow(() -> new IllegalStateException("Default role not found: " + DEFAULT_ROLE));

        User newUser = new User();
        newUser.setEmail(userInfo.email());
        newUser.setUsername(userInfo.name());
        newUser.setAvatar(userInfo.picture());
        newUser.setEnabled(true);
        newUser.setAuthorities(List.of(userRole));
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        return userService.save(newUser);
    }

    private String generateJwtToken(User user) {
        UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail())
                .password("")
                .authorities(user.getAuthorities())
                .build();
        return jwtUtils.generateTokenFromUsername(userDetails);
    }

    private String buildRedirectUrl(String jwt, User user) {
        return UriComponentsBuilder.fromUriString(redirectUri.trim())
                .queryParam("token", jwt)
                .queryParam("email", user.getEmail())
                .queryParam("username", user.getUsername())
                .build()
                .encode()
                .toUriString();
    }

    private void handleAuthenticationError(HttpServletResponse response, Exception e) throws IOException {
        String errorUrl = UriComponentsBuilder.fromUriString(redirectUri.replace("/oauth2/redirect", "/login"))
                .queryParam("error", "oauth2_error")
                .queryParam("message", "Authentication failed")
                .build()
                .encode()
                .toUriString();

        response.sendRedirect(errorUrl);
    }

    private record UserInfo(String email, String name, String picture) {}
}
