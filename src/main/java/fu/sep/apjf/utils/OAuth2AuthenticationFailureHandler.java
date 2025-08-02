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

        log.error("OAuth2 authentication failed: {}", exception.getMessage(), exception);

        try {
            String loginUrl = redirectUri.replace("/oauth2/redirect", "/login");
            String errorUrl = UriComponentsBuilder.fromUriString(loginUrl)
                    .queryParam("error", "oauth2_failed")
                    .queryParam("message", "Đăng nhập với Google thất bại")
                    .build()
                    .encode()
                    .toUriString();

            log.info("Redirecting to error URL: {}", errorUrl);
            getRedirectStrategy().sendRedirect(request, response, errorUrl);

        } catch (Exception e) {
            log.error("Failed to redirect after OAuth2 failure", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"error\":\"OAuth2 authentication failed\",\"message\":\"Đăng nhập với Google thất bại\"}");
            response.getWriter().flush();
        }
    }
}
