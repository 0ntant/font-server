package app.backendservice.dto;

import app.backendservice.model.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Date;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@Data
@Builder
public class UserSafeDto
{
    private int id;

    private String username;

    private String email;

    private String avatarPath;

    private boolean isEnabled;

    private boolean isAccountNonLocked;

    private  boolean isAccountNonExpired;

    private  boolean isCredentialsNonExpired;

    private Date createDate;

    private Date modifyDate;

    private List<ProjectUserDto> projects;

    private List<RoleUserDto> roles;

    public static UserSafeDto mapToUserSafeDto(User user)
    {
        return UserSafeDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarPath(user.getAvatarPath())
                .isEnabled(user.isEnabled())
                .isAccountNonLocked(user.isAccountNonLocked())
                .isAccountNonExpired(user.isAccountNonExpired())
                .isCredentialsNonExpired(user.isCredentialsNonExpired())
                .createDate(user.getCreateDate())
                .modifyDate(user.getModifyDate())
                .projects(user.getProjects().stream().map(ProjectUserDto::mapToProjectUserDto).toList())
                .roles(user.getRoles().stream().map(RoleUserDto::mapToRoleUserDto).toList())
                .build();
    }

    public static UserSafeDto mapSelfToUserSafeDto(User user)
    {
        return UserSafeDto.builder()
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarPath(user.getAvatarPath())
                .projects(user.getProjects().stream().map(ProjectUserDto::mapToProjectUserDto).toList())
                .build();
    }


    public static User mapToUserSafeDto(UserSafeDto user)
    {
        return User.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .avatarPath(user.getAvatarPath())
                .isEnabled(user.isEnabled())
                .isAccountNonLocked(user.isAccountNonLocked())
                .isAccountNonExpired(user.isAccountNonExpired())
                .isCredentialsNonExpired(user.isCredentialsNonExpired())
                .createDate(user.getCreateDate())
                .modifyDate(user.getModifyDate())
                .build();
    }

    public static UserSafeDto mapToUserRoles(User user)
    {
        return UserSafeDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .roles(user.getRoles().stream().map(RoleUserDto::mapToRoleUserDto).toList())
                .isEnabled(user.isEnabled())
                .isAccountNonLocked(user.isAccountNonLocked())
                .isAccountNonExpired(user.isAccountNonExpired())
                .isCredentialsNonExpired(user.isCredentialsNonExpired())
                .build();
    }

}
