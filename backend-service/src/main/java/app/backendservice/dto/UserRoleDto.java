package app.backendservice.dto;

import app.backendservice.model.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserRoleDto
{
    private int id;

    private String username;

    public static UserRoleDto mapToUserRoleDto(User user)
    {
        return new UserRoleDto(
                user.getId(),
                user.getUsername()
        );
    }
}
