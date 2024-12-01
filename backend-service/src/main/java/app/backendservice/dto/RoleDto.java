package app.backendservice.dto;

import app.backendservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@AllArgsConstructor
@Data
@Builder
public class RoleDto
{
    private int id;

    private String title;

    private List<UserRoleDto> users;

    public static RoleDto mapToRoleDto(Role role)
    {
        return RoleDto.builder()
                .id(role.getId())
                .title(role.getTitle())
                .users(role.getUsers().stream().map(UserRoleDto::mapToUserRoleDto).toList())
                .build();
    }
}
