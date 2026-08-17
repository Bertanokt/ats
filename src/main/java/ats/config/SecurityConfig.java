package ats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtGirisNoktasi jwtGirisNoktasi;
    private final YetkiHatasiYoneticisi yetkiHatasiYoneticisi;

    public SecurityConfig(JwtFilter jwtFilter, JwtGirisNoktasi jwtGirisNoktasi, YetkiHatasiYoneticisi yetkiHatasiYoneticisi) {
        this.jwtFilter = jwtFilter;
        this.jwtGirisNoktasi = jwtGirisNoktasi;
        this.yetkiHatasiYoneticisi = yetkiHatasiYoneticisi;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> {})
                .sessionManagement(o -> o.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(istek -> istek
                        // Herkese acik
                        .requestMatchers("/", "/api/auth/**", "/api/public/**").permitAll()

                        // Sadece ADMIN silebilir
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN")

                        // Gerisi: giris yapmis olmak yeterli
                        .anyRequest().authenticated()
                )
                .exceptionHandling(o -> o
                        .authenticationEntryPoint(jwtGirisNoktasi)
                        .accessDeniedHandler(yetkiHatasiYoneticisi)
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}