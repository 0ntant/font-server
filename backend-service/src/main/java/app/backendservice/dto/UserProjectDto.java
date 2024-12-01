package app.backendservice.dto;

import app.backendservice.model.User;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class UserProjectDto
{
    private int id;

    private String username;

    static public UserProjectDto mapToProjectUser(User user)
    {
        return new UserProjectDto(
                user.getId(),
                user.getUsername()
        );
    }
}
