package fu.sep.apjf.config;

import fu.sep.apjf.utils.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    // 1. PUBLIC ENDPOINTS - Cho tất cả mọi người (bao gồm cả POST, PUT) - Auth và Authentication
    private static final String[] AUTH_ENDPOINTS = {
            "/api/auth/**",
            "/oauth2/**",
            "/login/oauth2/**",
            "/api/media/**"
    };

    // 2. GUEST ENDPOINTS - Chỉ cho phép GET (cho khách không đăng nhập)
    private static final String[] PUBLIC_GET_ENDPOINTS = {
            "/api/courses",              // Xem danh sách khóa học
            "/api/courses/*/chapters",   // Xem chapters của course
            "/api/courses/top-rated",    // Xem top rated courses
            "/api/courses/*/reviews",    // Xem reviews của course
            "/api/topics",               // Xem danh sách topics
            "/api/posts",                // Xem danh sách posts (chỉ GET)
            "/api/posts/*",              // Xem chi tiết post và comments (chỉ GET)
            "/api/posts/*/comments",     // Xem comments của post (chỉ GET)
            "/api/chapters/**",          // Xem chapters
            "/api/units/**",             // Xem units
            "/api/materials/**",         // Xem materials
            "/api/learning-paths/**",    // Xem learning paths
            "/api/reviews/**"            // Xem reviews
    };

    // 3. USER ENDPOINTS - Dành cho role USER (và các role cao hơn)
    private static final String[] USER_ALLOWED_ENDPOINTS = {
            "/api/users/profile",        // Cập nhật profile, email, phone
            "/api/users/avatar",         // Upload avatar
            "/api/reviews",              // POST - Tạo review khóa học
            "/api/reviews/**",           // CRUD reviews của chính mình
            "/api/notifications/**",     // Xem thông báo
            "/api/posts",                // POST - Đăng post mới
            "/api/posts/*",              // PUT, DELETE - Cập nhật, xóa post
            "/api/comments/**",          // Comment posts
            "/api/post-likes/**",        // Like posts
            "/api/student/exams/**",     // Làm exam
            "/api/student/history/**",   // Xem lịch sử học tập
            "/api/courses/user",         // Khóa học của user
            "/api/courses/*/enroll"      // Đăng ký khóa học
    };

    // 4. STAFF ENDPOINTS - CRUD course, chapter, unit, material
    private static final String[] STAFF_ENDPOINTS = {
            "/api/courses",              // POST - Tạo course mới
            "/api/courses/*",            // PUT, PATCH, DELETE - CRUD courses
            "/api/courses/upload",       // Upload course image
            "/api/chapters/**",          // CRUD chapters
            "/api/units/**",             // CRUD units (trừ GET đã có ở PUBLIC_GET)
            "/api/materials/**",         // CRUD materials (trừ GET đã có ở PUBLIC_GET)
            "/api/questions/**",         // CRUD questions
            "/api/exams/**",             // CRUD exams (trừ student/exams và GET)
            "/api/options/**"            // CRUD options
    };

    // 5. MANAGER ENDPOINTS - Duyệt approval requests
    private static final String[] MANAGER_ENDPOINTS = {
            "/api/approval-requests/**"  // Duyệt các yêu cầu
    };

    // 6. ADMIN ENDPOINTS - Quản trị hệ thống
    private static final String[] ADMIN_ENDPOINTS = {
            "/api/admin/**"              // Quản trị hệ thống
    };

    private final OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    public SecurityConfig(
            @Lazy OAuth2UserService<OAuth2UserRequest, OAuth2User> oAuth2UserService,
            @Lazy OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler,
            @Lazy OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler) {
        this.oAuth2UserService = oAuth2UserService;
        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthEntryPointJwt unauthorizedHandler, JwtUtils jwtUtils) throws Exception {
        return http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml"
                        ).permitAll()
                        .requestMatchers("/actuator/health", "/actuator/health/**", "/actuator/info").permitAll()
                        .requestMatchers(AUTH_ENDPOINTS).permitAll()  // Allow all methods for auth endpoints
                        .requestMatchers(HttpMethod.GET, PUBLIC_GET_ENDPOINTS).permitAll()  // Only GET for public endpoints
                        .requestMatchers(USER_ALLOWED_ENDPOINTS).hasAnyRole("USER", "MANAGER", "ADMIN", "STAFF")
                        .requestMatchers(STAFF_ENDPOINTS).hasAnyRole("STAFF", "MANAGER", "ADMIN")
                        .requestMatchers(MANAGER_ENDPOINTS).hasAnyRole("MANAGER", "ADMIN")
                        .requestMatchers(ADMIN_ENDPOINTS).hasRole("ADMIN")
                        .anyRequest().hasAnyRole("MANAGER", "ADMIN", "STAFF"))
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(unauthorizedHandler)
                        .accessDeniedHandler(new CustomAccessDeniedHandler()))
                .oauth2Login(oauth2 -> oauth2
                        .authorizationEndpoint(endpoint -> endpoint
                                .authorizationRequestRepository(authorizationRequestRepository()))
                        .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserService))
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                        .failureHandler(oAuth2AuthenticationFailureHandler))
                .addFilterBefore(authenticationJwtTokenFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class)
                .build();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration cfg = new CorsConfiguration();
        cfg.setAllowedOrigins(List.of("*"));
        cfg.setAllowedMethods(List.of("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS"));
        cfg.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource src = new UrlBasedCorsConfigurationSource();
        src.registerCorsConfiguration("/**", cfg);
        return src;
    }

    @Bean
    public AuthTokenFilter authenticationJwtTokenFilter(JwtUtils jwtUtils) {
        return new AuthTokenFilter(jwtUtils);
    }

    @Bean
    public AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository() {
        return new HttpSessionOAuth2AuthorizationRequestRepository();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}
