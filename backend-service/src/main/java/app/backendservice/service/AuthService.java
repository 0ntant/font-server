package app.backendservice.service;

import app.backendservice.dto.TokenDto;
import app.backendservice.dto.TokensDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.Token;
import app.backendservice.model.User;
import app.backendservice.model.UserTokens;
import app.backendservice.repository.UserRepository;
import app.backendservice.repository.UserTokensRepository;
import app.backendservice.utils.HashUtil;
import app.backendservice.utils.JwtNimbusUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.ArrayList;
import java.util.HashMap;
import java.lang.String;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService
{
    private final JwtNimbusUtil jwtNimbusUtil;

    private final HashUtil hashUtil;

    private final UserRepository userRepository;

    private final UserTokensRepository userTokensRepository;

    private final HttpServletRequest request;

    private  User findUserById(int id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id=%s not found", id)));

    }

    private User findByUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username=%s not found", username)));
    }

    private String findByJwtRefreshToken(Integer userId)
    {
        String jwtTokenHash = this.findUserById(userId).getUserTokens().getJwtRefresh();

        if(jwtTokenHash == null)
        {
            log.warn("Jwt token in null for userId: {}", userId);
            throw new AuthenticationCredentialsNotFoundException("you must be authenticated");
        }

        return jwtTokenHash;
    }

    private void verifyDBToken(String jwtToken,String jwtTokenHash)
    {
        if(!hashUtil.doPasswordsMatch(jwtToken, jwtTokenHash))
        {
            log.warn("Jwt tokens don't match request:{} , db:{}",jwtToken,jwtTokenHash);
            throw new AuthenticationCredentialsNotFoundException("you must be authenticated");
        }

    }

    private String getBeaverToken()
    {
        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization != null && authorization.startsWith("Bearer ")) {
            return authorization.replace("Bearer ", "");
        }
        return null;
    }

    @Transactional
    public TokensDto login(Authentication authentication)
    {
        User user = this.findByUsername(authentication.getName());
        Token refreshToken = jwtNimbusUtil.createRefreshToken(user.getId(), authentication);
        String refreshTokenString = jwtNimbusUtil.refreshTokenJweStringSerialize(refreshToken);
        UserTokens userTokens = userTokensRepository.findById(user.getId())
                .orElse(
                        UserTokens.builder()
                        .id(user.getId())
                        .jwtRefresh("")
                        .user(user)
                        .build()
                );

        userTokens.setJwtRefresh(hashUtil.bCryptHash(refreshTokenString));
        user.setUserTokens(userTokens);
        userRepository.save(user);

        Token accessToken = jwtNimbusUtil.createAccessToken(refreshToken);
        return new TokensDto
                (
                    new TokenDto(jwtNimbusUtil.accessTokenJwsStringSerialize(accessToken), accessToken.getCreateAt(), accessToken.getExpiresAt()),
                    new TokenDto(refreshTokenString,refreshToken.getCreateAt(), refreshToken.getExpiresAt())
                );
    }

    public TokenDto refreshToken()
    {
        User user = this.findUserById(Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));
        Token refreshToken = jwtNimbusUtil.refreshTokenJwsStringDeserializer(this.getBeaverToken());
        Token accessToken = jwtNimbusUtil.createAccessToken(refreshToken);

        return new TokenDto(jwtNimbusUtil.accessTokenJwsStringSerialize(accessToken), accessToken.getCreateAt(), accessToken.getExpiresAt());
    }

    public String logout()
    {
        User user = this.findUserById(Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()));
        user.getUserTokens().setJwtRefresh(null);
        userRepository.save(user);

        return "Success logout";
    }

    public Authentication convert()
    {
        String token = this.getBeaverToken();
        Token accessToken = jwtNimbusUtil.accessTokenJwsStringDeserializer(token);
        if (accessToken != null)
        {
            List<GrantedAuthority> listAuthorities = AuthorityUtils.createAuthorityList(accessToken.getAuthorities());
            return new UsernamePasswordAuthenticationToken(
                    accessToken.getSubjectId(),null, listAuthorities);
        }

        Token refreshToken = jwtNimbusUtil.refreshTokenJwsStringDeserializer(token);
        if (refreshToken != null)
        {
            verifyDBToken(token, this.findByJwtRefreshToken(refreshToken.getSubjectId()));

            List<GrantedAuthority> listAuthorities = AuthorityUtils.createAuthorityList(refreshToken.getAuthorities());
            return new UsernamePasswordAuthenticationToken(
                    refreshToken.getSubjectId(),null, listAuthorities);
        }

        return null;
    }

    public Authentication convertRefreshToken()
    {
        String token = this.getBeaverToken();
        Token refreshToken = jwtNimbusUtil.refreshTokenJwsStringDeserializer(token);
        if (refreshToken != null)
        {
            verifyDBToken(token, this.findByJwtRefreshToken(refreshToken.getSubjectId()));

            List<GrantedAuthority> listAuthorities = AuthorityUtils.createAuthorityList(refreshToken.getAuthorities());
            return new UsernamePasswordAuthenticationToken(
                    refreshToken.getSubjectId(),null, listAuthorities);
        }
        return null;
    }

    public Authentication convertAccessToken()
    {
        String token = this.getBeaverToken();
        Token accessToken = jwtNimbusUtil.accessTokenJwsStringDeserializer(token);
        if (accessToken != null)
        {
            List<GrantedAuthority> listAuthorities = AuthorityUtils.createAuthorityList(accessToken.getAuthorities());
            return new UsernamePasswordAuthenticationToken(
                    accessToken.getSubjectId(),null, listAuthorities);
        }
        return null;
    }


}
