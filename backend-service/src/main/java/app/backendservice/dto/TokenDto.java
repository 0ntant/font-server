package app.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;

@Data

public class TokenDto
{
    private  String token;

    private  Instant createAt;

    private  Instant expiresAt;

    @JsonCreator
    public TokenDto(@JsonProperty("token") String token,
                    @JsonProperty("createAt") Instant createAt,
                    @JsonProperty("expiresAt") Instant expiresAt)
    {
        this.token = token;
        this.createAt = createAt;
        this.expiresAt = expiresAt;
    }

}
