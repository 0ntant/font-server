package app.backendservice.exception.handler.auth;

import app.backendservice.exception.message.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AccessDeniedHandlerSession implements AccessDeniedHandler
{

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException
    {
        ZonedDateTime timestamp = ZonedDateTime.now();
        HttpStatus status = HttpStatus.FORBIDDEN;
        String requestURI = request.getServletPath();
        ApiError apiError = ApiError.builder()
                .timestamp(timestamp)
                .status(status.value())
                .error(accessDeniedException.getMessage())
                .path(requestURI) // Используем полученный URI запроса
                .build();

        log.error("{} {} {}",SecurityContextHolder.getContext().getAuthentication().getName() ,accessDeniedException.getMessage(),requestURI);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String jsonErrorResponse = objectMapper.writeValueAsString(apiError);

        response.setContentType("application/json");
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.getWriter().write(jsonErrorResponse);
    }
}