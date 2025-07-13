package fu.sep.apjf.utils;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
@Slf4j
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Value("${app.oauth2.redirectUri:http://localhost:5173/oauth2/redirect}")
    private String redirectUri;

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                      AuthenticationException exception) throws IOException {
        log.error("OAuth2 authentication failed: {}", exception.getMessage());

        try {
            String loginUrl = redirectUri.replace("/oauth2/redirect", "/login");
            String errorUrl = UriComponentsBuilder.fromUriString(loginUrl)
                    .queryParam("error", "oauth2_failed")
                    .queryParam("message", "Google authentication failed")
                    .build()
                    .encode()
                    .toUriString();

            getRedirectStrategy().sendRedirect(request, response, errorUrl);
        } catch (Exception e) {
            log.error("Failed to redirect after OAuth2 failure", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("OAuth2 authentication failed");
        }
    }
}
