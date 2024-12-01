package app.backendservice.config;

import app.backendservice.exception.handler.auth.AccessDeniedHandlerSession;
import app.backendservice.exception.handler.auth.AuthEntryPointSession;
import app.backendservice.filters.JwtAuthFilter;
import app.backendservice.filters.LogFilter;
import app.backendservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.DefaultSecurityFilterChain;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration
{
    final private UserService userService;

    final private AccessDeniedHandlerSession accessDeniedHandlerSession;

    final private AuthEntryPointSession authEntryPointSession;

    final private JwtAuthFilter jwtAuthFilter;

    @Autowired
    protected void configure(AuthenticationManagerBuilder auth) throws Exception
    {
        DaoAuthenticationProvider daoAuthenticationProvider = new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(userService);
        daoAuthenticationProvider.setPasswordEncoder(new BCryptPasswordEncoder());
        auth
            .authenticationProvider(daoAuthenticationProvider);

    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public DefaultSecurityFilterChain filterChains(HttpSecurity http) throws Exception
    {
        http.csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers("/", "/user/api/v1/reg-user", "/auth/api/v1/login", "/error").permitAll()

                    .requestMatchers("/user/api/v1/self-edit").authenticated()

                    .requestMatchers("/auth/api/v1/refresh").hasAuthority("JWT_REFRESH")
                    .requestMatchers("/auth/api/v1/logout").hasAuthority("JWT_LOGOUT")

                    .requestMatchers("/font/form").hasRole("DEVELOPER")
                    .requestMatchers(req-> req.getRequestURI().contains("save")).hasRole("DEVELOPER")
                    .requestMatchers(req-> req.getRequestURI().contains("delete")).hasRole("DEVELOPER")
                    .requestMatchers(req-> req.getRequestURI().contains("create")).hasRole("DEVELOPER")
                    .requestMatchers(req-> req.getRequestURI().contains("set")).hasRole("DEVELOPER")
                    .requestMatchers(req-> req.getRequestURI().contains("edit")).hasRole("DEVELOPER")

                    .requestMatchers("/role/api/v1/set-role-manager/*").hasAnyRole("MANAGER", "ADMIN", "DEVELOPER")
                    .requestMatchers("/role/api/v1/set-role-admin/*").hasAnyRole("ADMIN", "DEVELOPER")
                    .requestMatchers("/role/api/v1/set-role-developer/*").hasAnyRole("DEVELOPER")

                    .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex.accessDeniedHandler(accessDeniedHandlerSession))
            .exceptionHandling(ex -> ex.authenticationEntryPoint(authEntryPointSession))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
            .sessionManagement((session) -> session
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            );

        return http.build();
    }
}
