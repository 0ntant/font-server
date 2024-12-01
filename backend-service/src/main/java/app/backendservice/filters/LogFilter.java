
package app.backendservice.filters;

import java.io.IOException;
import java.util.UUID;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;


@Component
@Slf4j
public class LogFilter extends OncePerRequestFilter 
{
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
    throws ServletException, IOException 
    {
        UUID trackingHeaderValue = UUID.randomUUID();                 
        long startTime           = System.currentTimeMillis();
        try 
        {
            MDC.put("userId", SecurityContextHolder.getContext().getAuthentication().getName());
            filterChain.doFilter(request, response);
        } 
        finally 
        {
            long timeTaken = System.currentTimeMillis() - startTime;
            log.info(
                "METHOD={}; REQUESTURI={}; RESPONSE CODE={}; TIME_MS={}",
                request.getMethod(), request.getRequestURI(), response.getStatus(),
                timeTaken
            );
            MDC.clear();
        }
    }
}
