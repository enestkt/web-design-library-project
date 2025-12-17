package com.project.library.config;

import com.project.library.security.CustomUserDetailsService;
import com.project.library.security.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtAuthFilter jwtAuthFilter;

    // --- 1. FİLTRE: ADMIN PANELİ & WEB SAYFALARI (Thymeleaf - Session Tabanlı) ---
    @Bean
    @Order(1)
    public SecurityFilterChain adminFilterChain(HttpSecurity http) throws Exception {
        http
                // Bu zincir sadece admin paneli, login sayfası ve statik dosyalar (css/js) için çalışır
                .securityMatcher("/admin/**", "/login", "/css/**", "/js/**", "/images/**", "/")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/").permitAll() // Giriş ve statik kaynaklar serbest
                        .requestMatchers("/admin/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_USER") // Admin paneline kimler girebilir
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login") // Backend'deki templates/login.html
                        .loginProcessingUrl("/login") // Formun POST edileceği yer
                        .defaultSuccessUrl("/", true) // Başarılı girişte ana sayfaya git
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }

    // --- 2. FİLTRE: API (React - JWT Tabanlı) ---
    @Bean
    @Order(2)
    public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
        http
                // /api ile başlayan her şey ve Swagger bu zincire girer
                .securityMatcher("/api/**", "/swagger-ui/**", "/v3/api-docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                // CORS ayarını burada bağlıyoruz (Aşağıdaki metodu kullanır)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/books/init").permitAll() // Auth ve Init serbest
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll() // Swagger dokümanı serbest
                        .anyRequest().authenticated() // Diğer tüm API istekleri token ister
                )
                // API'de Session tutmuyoruz (Stateless)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    // ⭐ KRİTİK AYAR: CORS YAPILANDIRMASI ⭐
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Hem Lokalde hem Canlıda (Render) React'in erişimine izin veriyoruz
        configuration.setAllowedOrigins(List.of(
                "http://localhost:3000",             // Lokal React
                "https://library-management-frontend-murex.vercel.app"   // İlerdeki Canlı React Adresin
        ));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}