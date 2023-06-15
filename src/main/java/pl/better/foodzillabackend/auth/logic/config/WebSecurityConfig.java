package pl.better.foodzillabackend.auth.logic.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import pl.better.foodzillabackend.auth.logic.service.token.JWTAuthorizationFilter;
import pl.better.foodzillabackend.auth.logic.service.token.TokenUtils;
import pl.better.foodzillabackend.customer.logic.service.CustomerService;

@Configuration
@EnableGlobalMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final TokenUtils tokenUtils;
    private final CustomerService service;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .headers().frameOptions().disable()
                .and()
                .cors().and().csrf().disable()
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/graphql")
                        .permitAll()
                        .anyRequest().denyAll())
                .addFilter(new JWTAuthorizationFilter(authenticationManagerBean(httpSecurity), tokenUtils))
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return httpSecurity.build();
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder = http
                .getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder
                .userDetailsService(service)
                .passwordEncoder(passwordEncoder());

        return authenticationManagerBuilder.build();
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
