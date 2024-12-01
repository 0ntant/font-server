package app.backendservice.filters;

import app.backendservice.service.AuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

import app.backendservice.service.UserService;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter
{

    private final RequestMatcher requestMatcherRefresh = new AntPathRequestMatcher("/auth/api/v1/refresh", HttpMethod.GET.name());
    private final RequestMatcher requestMatcherLogout = new AntPathRequestMatcher("/auth/api/v1/logout", HttpMethod.GET.name());

    private final AuthService authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException
    {
        UUID trackingHeaderValue = UUID.randomUUID();
        String authHeader = request.getHeader("Authorization");
        String token = null;
        MDC.put("tracking", trackingHeaderValue.toString());
        if (authHeader != null && authHeader.startsWith("Bearer "))
        {
            token = authHeader.substring(7);
        }
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null)
        {
            //SecurityContextHolder.getContext().setAuthentication(authService.convert());
            if(this.requestMatcherRefresh.matches(request) || this.requestMatcherLogout.matches(request))
            {
                SecurityContextHolder.getContext().setAuthentication(authService.convertRefreshToken());
            }
            else
            {
                SecurityContextHolder.getContext().setAuthentication(authService.convertAccessToken());
            }
        }
        filterChain.doFilter(request, response);
    }
}
