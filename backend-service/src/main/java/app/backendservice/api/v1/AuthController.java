package app.backendservice.api.v1;


import app.backendservice.dto.TokenDto;
import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "auth/api/v1",
        produces={"application/json"})
@Slf4j
public class AuthController
{
    final private AuthService authService;

    final private AuthenticationManager authenticationManager;

    @PostMapping("login")
    public TokensDto authenticateAndGetToken(@RequestBody UserToAuth userToAuth)
    {
        log.info("Try to login: {}",userToAuth.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        userToAuth.getUsername(), userToAuth.getPassword()));
        if (authentication.isAuthenticated())
        {
            return authService.login(authentication);
        }
        else
        {
            throw new UsernameNotFoundException("invalid user request");
        }
    }

    @GetMapping("refresh")
    public TokenDto refreshAccessToken()
    {
       return authService.refreshToken();
    }

    @GetMapping("logout")
    public String logout()
    {
        return authService.logout();
    }
}
