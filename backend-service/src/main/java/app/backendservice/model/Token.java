package app.backendservice.model;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Data
public class Token
{
    private final Integer subjectId;

    private final List<String> authorities;

    private final Instant createAt;

    private final Instant expiresAt;
}
