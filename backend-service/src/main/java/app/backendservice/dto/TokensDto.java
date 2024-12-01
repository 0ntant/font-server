package app.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@NoArgsConstructor
@Data
public class TokensDto
{
    private  TokenDto accessToken;

    private  TokenDto refreshToken;

    @JsonCreator
    public TokensDto(@JsonProperty("accessToken") TokenDto accessToken,
                     @JsonProperty("refreshToken") TokenDto refreshToken)
    {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
    }
}
