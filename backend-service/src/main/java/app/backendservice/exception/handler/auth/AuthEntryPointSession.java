package app.backendservice.exception.handler.auth;

import app.backendservice.exception.message.ApiError;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.ServletException;

import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.ZonedDateTime;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
@Slf4j
public class AuthEntryPointSession implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException
    {
        ZonedDateTime timestamp = ZonedDateTime.now();
        HttpStatus requeStatus = HttpStatus.UNAUTHORIZED;
        String path = request.getServletPath();
        ApiError apiError = ApiError.builder()
                .timestamp(timestamp)
                .status(requeStatus.value())
                .error(authException.getMessage())
                .path(path)
                .build();

        log.error("{} {}",authException.getMessage(), path);

        // Создаем ObjectMapper с поддержкой типов даты и времени Java 8
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonErrorResponse = objectMapper.writeValueAsString(apiError);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.getWriter().write(jsonErrorResponse);
    }
}