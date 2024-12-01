package app.backendservice.dto;


import app.backendservice.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class UserToRegistration
{
    @NotNull(message = "Username must be not null")
    @Pattern(regexp = "(.*[a-z]+.*)", message = "Password must contain at least one lowercase letter")
    @Size(min = 3, message = "Username must contain at least 3 symbols ")
    @Size(max = 255, message = "Username is too long")
    final private String username;

    @NotNull(message = "Password must be not null")
    @Pattern(regexp = "(.*[0-9]+.*)", message = "Password must contain at least one number")
    @Pattern(regexp = "(.*[a-z]+.*)", message = "Password must contain at least one lowercase letter")
    @Pattern(regexp = "(.*[A-Z]+.*)", message = "Password must contain at least one uppercase letter")
    @Pattern(regexp = "(.*[!@#%}{:;^|$&*()_+=,./<>?~`]+.*)", message = "Password must contain at least one special character")
    @Size(min = 14 , message = "Set minimum password length to at least a value of 14")
    @Size(max = 255, message = "Password is too long")
    final private String password;

    @NotNull(message = "Email must be not null")
    @Email(message = "Provide correct email address")
    final private String email;

    @Size(max = 255, message = "AvatarPath is too long")
    @Size(min = 3, message = "avatarPath must contain at least 3 symbols ")
    final private String avatarPath;
}
