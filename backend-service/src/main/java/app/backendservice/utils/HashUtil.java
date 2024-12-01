package app.backendservice.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;


@Slf4j
@Component
public class HashUtil
{
    public String bCryptHash(String stringToHash)
    {
        String hashedString = null;
        try
        {
            hashedString=  new BCryptPasswordEncoder().encode(stringToHash);
        }
        catch ( IllegalArgumentException ex)
        {
            log.error(ex.getMessage());
        }

        return hashedString;
    }


    public Boolean doPasswordsMatch(String plainString, String encodedString)
    {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(plainString, encodedString);
    }
}
