package fu.sep.apjf.service;

import fu.sep.apjf.entity.Authority;
import fu.sep.apjf.entity.User;
import fu.sep.apjf.repository.AuthorityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OAuth2UserServiceImpl implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserService userService;
    private final AuthorityRepository authorityRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String GOOGLE_EMAIL_ATTRIBUTE = "email";
    private static final String GOOGLE_NAME_ATTRIBUTE = "name";
    private static final String GOOGLE_PICTURE_ATTRIBUTE = "picture";
    private static final String DEFAULT_ROLE = "ROLE_USER";
    private static final String DEFAULT_AVATAR_URL = "https://engineering.usask.ca/images/no_avatar.jpg";
    private static final String DEFAULT_USERNAME = "Google User";

    @Override
    @Transactional
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        try {
            OAuth2User oauth2User = new DefaultOAuth2UserService().loadUser(userRequest);
            Map<String, Object> attributes = oauth2User.getAttributes();

            String email = extractAndValidateEmail(attributes);
            String name = extractName(attributes);
            String picture = extractPicture(attributes);

            User user = processUserAuthentication(email, name, picture);
            Collection<GrantedAuthority> authorities = createGrantedAuthorities(user);

            return new DefaultOAuth2User(authorities, attributes, GOOGLE_NAME_ATTRIBUTE);
        } catch (OAuth2AuthenticationException e) {
            throw e;
        } catch (Exception e) {
            log.error("OAuth2 authentication error: {}", e.getMessage(), e);
            throw new OAuth2AuthenticationException("OAuth2 authentication failed: " + e.getMessage());
        }
    }

    private String extractAndValidateEmail(Map<String, Object> attributes) {
        String email = (String) attributes.get(GOOGLE_EMAIL_ATTRIBUTE);
        if (!StringUtils.hasText(email)) {
            throw new OAuth2AuthenticationException("Email not provided by Google");
        }
        return email.toLowerCase().trim();
    }

    private String extractName(Map<String, Object> attributes) {
        String name = (String) attributes.get(GOOGLE_NAME_ATTRIBUTE);
        return StringUtils.hasText(name) ? name.trim() : DEFAULT_USERNAME;
    }

    private String extractPicture(Map<String, Object> attributes) {
        String picture = (String) attributes.get(GOOGLE_PICTURE_ATTRIBUTE);
        return StringUtils.hasText(picture) ? picture : DEFAULT_AVATAR_URL;
    }

    private User processUserAuthentication(String email, String name, String picture) {
        Optional<User> existingUser = userService.findByEmail(email);

        if (existingUser.isPresent()) {
            return updateExistingUserIfNeeded(existingUser.get(), name, picture);
        } else {
            return createNewUserFromOAuth2(email, name, picture);
        }
    }

    private User updateExistingUserIfNeeded(User user, String name, String picture) {
        boolean needsUpdate = false;

        if (!name.equals(user.getUsername())) {
            user.setUsername(name);
            needsUpdate = true;
        }

        if (!DEFAULT_AVATAR_URL.equals(picture) && !picture.equals(user.getAvatar())) {
            user.setAvatar(picture);
            needsUpdate = true;
        }

        if (!user.isEnabled()) {
            user.setEnabled(true);
            needsUpdate = true;
        }

        return needsUpdate ? userService.save(user) : user;
    }

    private User createNewUserFromOAuth2(String email, String name, String picture) {
        Authority userRole = authorityRepository.findByAuthority(DEFAULT_ROLE)
                .orElseThrow(() -> new OAuth2AuthenticationException("Default role not found: " + DEFAULT_ROLE));

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setUsername(name);
        newUser.setAvatar(picture);
        newUser.setEnabled(true);
        newUser.setAuthorities(List.of(userRole));
        newUser.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));

        return userService.save(newUser);
    }

    private Collection<GrantedAuthority> createGrantedAuthorities(User user) {
        if (user.getAuthorities() == null || user.getAuthorities().isEmpty()) {
            return Collections.singletonList(new SimpleGrantedAuthority(DEFAULT_ROLE));
        }

        return user.getAuthorities().stream()
                .map(authority -> new SimpleGrantedAuthority(authority.getAuthority()))
                .map(GrantedAuthority.class::cast)
                .toList();
    }
}