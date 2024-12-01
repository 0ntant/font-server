package app.backendservice.utils;

import app.backendservice.model.Token;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.DirectDecrypter;
import com.nimbusds.jose.crypto.DirectEncrypter;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jose.jwk.OctetSequenceKey;
import com.nimbusds.jwt.EncryptedJWT;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.beans.factory.annotation.Value;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedList;
import java.util.Date;

@Slf4j
@Data
@Component
public class JwtNimbusUtil
{
    private static Duration tokenAccessTtl = Duration.ofHours(1);
    private static Duration tokenRefreshTtl = Duration.ofDays(1);

    private String accessTokenKey;

    private String  refreshTokenKey;

    private JWSSigner jwsSigner;

    private JWEEncrypter jweEncrypter;

    private JWSVerifier jwsVerifier;

    private JWEDecrypter jweDecrypter;

    private JWSAlgorithm jwsAlgorithm = JWSAlgorithm.HS256;

    private JWEAlgorithm jweAlgorithm = JWEAlgorithm.DIR;

    private EncryptionMethod encryptionMethod = EncryptionMethod.A128GCM;

    @Autowired
    public JwtNimbusUtil(
            @Value("${jwt.access-token-key}") String accessTokenKey,
            @Value("${jwt.refresh-token-key}") String  refreshTokenKey
    )
    {
        try
        {
            this.jwsSigner    = new MACSigner(OctetSequenceKey.parse(accessTokenKey));
            this.jweEncrypter = new DirectEncrypter(OctetSequenceKey.parse(refreshTokenKey));
            this.jwsVerifier  = new MACVerifier(OctetSequenceKey.parse(accessTokenKey));
            this.jweDecrypter = new DirectDecrypter(OctetSequenceKey.parse(refreshTokenKey));
        }
        catch (ParseException | JOSEException exception)
        {
            log.error(exception.getMessage(), exception);
        }
    }


    public Token createRefreshToken(int userId,Authentication authentication)
    {
        LinkedList<String> authorities = new LinkedList<String>();

        authorities.add("JWT_REFRESH");
        authorities.add("JWT_LOGOUT");
        authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .map(authority -> "GRANT_" + authority)
                .forEach(authorities::add);

        Instant now = Instant.now();
        return new Token(userId, authorities, now, now.plus(tokenRefreshTtl));
    }

    public Token createAccessToken (Token refreshToken)
    {
        Instant now = Instant.now();
        return new Token( refreshToken.getSubjectId(),
                refreshToken.getAuthorities().stream()
                        .filter(authority -> authority.startsWith("GRANT_"))
                        .map(authority -> authority.replace("GRANT_", ""))
                        .toList(), now, now.plus(tokenAccessTtl));
    }

    public String accessTokenJwsStringSerialize(Token token)
    {
        JWSHeader jwsHeader = new JWSHeader.Builder(this.jwsAlgorithm)
                .build();

        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(token.getSubjectId().toString())
                .issueTime(Date.from(token.getCreateAt()))
                .expirationTime(Date.from(token.getExpiresAt()))
                .claim("authorities", token.getAuthorities())
                .build();
        SignedJWT signedJWT = new SignedJWT(jwsHeader, claimsSet);
        try
        {
            signedJWT.sign(this.jwsSigner);

            return signedJWT.serialize();
        }
        catch (JOSEException exception) {
            log.error(exception.getMessage(), exception);
        }

        return null;
    }

    public String refreshTokenJweStringSerialize(Token token)
    {
        JWEHeader jweHeader = new JWEHeader.Builder(this.jweAlgorithm, this.encryptionMethod)
                .build();
        JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                .subject(token.getSubjectId().toString())
                .issueTime(Date.from(token.getCreateAt()))
                .expirationTime(Date.from(token.getExpiresAt()))
                .claim("authorities", token.getAuthorities())
                .build();
        EncryptedJWT encryptedJWT = new EncryptedJWT(jweHeader, claimsSet);
        try
        {
            encryptedJWT.encrypt(this.jweEncrypter);
            return encryptedJWT.serialize();
        }
        catch (JOSEException exception)
        {
            log.error(exception.getMessage(), exception);
        }

        return null;
    }

    public Token accessTokenJwsStringDeserializer(String token)
    {
        try
        {
            SignedJWT signedJWT = SignedJWT.parse(token);
            if (signedJWT.verify(this.jwsVerifier))
            {
                JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

                return new Token(Integer.valueOf(claimsSet.getSubject()),
                        claimsSet.getStringListClaim("authorities"),
                        claimsSet.getIssueTime().toInstant(),
                        claimsSet.getExpirationTime().toInstant());
            }
        }
        catch (ParseException | JOSEException exception)
        {
            log.error(exception.getMessage(), exception);
        }
        return null;
    }

    public Token refreshTokenJwsStringDeserializer(String token)
    {
        try
        {
            EncryptedJWT encryptedJWT = EncryptedJWT.parse(token);
            encryptedJWT.decrypt(this.jweDecrypter);
            JWTClaimsSet claimsSet = encryptedJWT.getJWTClaimsSet();

            return new Token(Integer.valueOf(claimsSet.getSubject()),
                    claimsSet.getStringListClaim("authorities"),
                    claimsSet.getIssueTime().toInstant(),
                    claimsSet.getExpirationTime().toInstant());
        }
        catch (ParseException | JOSEException exception)
        {
            log.error(exception.getMessage(), exception);
        }
        return  null;
    }
}
