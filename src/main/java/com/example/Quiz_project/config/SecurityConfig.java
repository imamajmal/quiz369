package com.example.Quiz_project.config;

import com.example.Quiz_project.filter.JwtFilter;
import com.example.Quiz_project.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .authenticationProvider(authProvider())

                // ✅ ✅ FORM LOGIN (Browser)
                .formLogin(form -> form
                        .loginPage("/login")               // Show login.html
                        .loginProcessingUrl("/login")      // POST from HTML form
                        .defaultSuccessUrl("/redirect", true) // redirect based on role
                        .permitAll()
                )

                // ✅ LOGOUT for browser
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .permitAll()
                )

                // ✅ PUBLIC ENDPOINTS
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/register", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/web/**").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")  
                        .requestMatchers("/participant/**").hasRole("PARTICIPANT")
                        .requestMatchers("/admin/quiz/**").hasAuthority("ADMIN")
                        .requestMatchers("/admin/question/**", "/admin/quiz/**").hasAuthority("ADMIN")
                        .requestMatchers("/quiz/**").permitAll()
                        .requestMatchers("/quizresult/**").permitAll()
                        .requestMatchers("/emailview/**").permitAll() 
                        .requestMatchers("/web/password/**").permitAll()
                        .requestMatchers("/admin/dashboard/**").permitAll()
                         .requestMatchers("/**").permitAll()  




                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/email/**").permitAll()
                        .requestMatchers("/notifications/registration").permitAll()
                        .requestMatchers("/notifications/password-reset").permitAll()

                        // ✅ ADMIN AREAS
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")
                        .requestMatchers("/notifications/quiz-result").hasAuthority("ADMIN")
                        .requestMatchers("/notifications/send").hasAuthority("ADMIN")

                        // ✅ PARTICIPANT AREAS
                        .requestMatchers("/participant/**").hasAuthority("PARTICIPANT")
                        .requestMatchers("/submission/**").hasAuthority("PARTICIPANT")

                        // ✅ other URLs need login
                        .anyRequest().authenticated()
                )

                // ✅ JWT filter for REST API
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
