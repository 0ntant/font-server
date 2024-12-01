package app.backendservice.dto;

import app.backendservice.model.Role;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class RoleUserDto
{
    private int id;

    private String title;

    public static RoleUserDto mapToRoleUserDto(Role role)
    {
        return new RoleUserDto(
                role.getId(),
                role.getTitle()
        );
    }
}
