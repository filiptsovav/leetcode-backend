package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.example.demo.security.JwtAuthenticationFilter;

import java.util.Arrays;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Autowired
    private UserDetailsService userDetailsService;

    // Password encoder
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // DaoAuthenticationProvider связывает userDetailsService и passwordEncoder
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    // AuthenticationManager (используем стандартный способ через AuthenticationManager)
    @Bean
    public AuthenticationManager authenticationManager(org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    // CORS configuration source — здесь настраиваем allowedOriginPatterns и exposed headers
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Разрешённые origin'ы для разработки. Добавь/измени по необходимости.
        configuration.setAllowedOriginPatterns(Arrays.asList(
                "http://localhost:5173",    // Vite (default)
                "http://100.90.167.69:5173",
                "http://localhost:3000",    // React dev server (если используешь)
                "http://100.90.167.69:3000",
                "http://100.*.*.*:*",
                "http://10.*.*.*:*"
        ));

        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*")); // разрешаем все заголовки (Authorization, Content-Type и т.д.)
        configuration.setAllowCredentials(true); // Важно для передачи cookie
        // Экспонируем заголовки, полезно для отладки и если клиент читает заголовки ответа
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "Authorization"));

        // Применяем конфиг ко всему приложению
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authenticationProvider(authenticationProvider())
            .authorizeHttpRequests(auth -> auth
                // публичные эндпоинты
                .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()

                // критично: разрешаем SockJS / websocket static endpoints и iframe/jsonp/info
                // это нужно, чтобы SockJS transport (iframe/jsonp/eventsource) мог загрузить свои ресурсы
                .requestMatchers("/ws/**").permitAll()

                // остальные требуют аутентификации
                .anyRequest().authenticated()
            )
            // ВАЖНО: отключаем frameOptions чтобы iframe от sockjs не блокировался
            .headers(headers -> headers.frameOptions().disable())

            // добавляем JWT фильтр как раньше
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // // Основной security filter chain
    // @Bean
    // public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    //     http
    //         .cors(cors -> cors.configurationSource(corsConfigurationSource())) // подключаем CORS
    //         .csrf(csrf -> csrf.disable()) // для JWT обычно CSRF отключают
    //         .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // stateless
    //         .authenticationProvider(authenticationProvider()) // наш DaoAuthenticationProvider
    //         .authorizeHttpRequests(auth -> auth
    //                 // публичные эндпоинты (регистрация/логин, swagger/openapi если нужно)
    //                 .requestMatchers("/auth/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
    //                 // сообщеньки
    //                 .requestMatchers("/ws/**", "/ws").permitAll()
    //                 // остальные требуют аутентификации
    //                 .anyRequest().authenticated()
    //         )
    //         // добавляем фильтр JWT перед UsernamePasswordAuthenticationFilter
    //         .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    //     return http.build();
    // }
}
