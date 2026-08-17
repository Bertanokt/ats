package ats.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * H2 Console'u sadece dev profilinde erisime acar.
 * Prod'da bu bean hic olusmaz, yani console disari kapalidir.
 */
@Configuration
@Profile("dev")
public class H2ConsoleSecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/h2-console/**")
                .csrf(csrf -> csrf.disable())
                .headers(o -> o.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(istek -> istek.anyRequest().permitAll());

        return http.build();
    }
}
