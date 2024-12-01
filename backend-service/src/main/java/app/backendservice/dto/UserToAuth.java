package app.backendservice.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserToAuth
{
    private final String username;

    private final String password;
}
