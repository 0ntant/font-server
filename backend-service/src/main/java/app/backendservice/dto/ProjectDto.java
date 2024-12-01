package app.backendservice.dto;

import app.backendservice.model.Project;

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
public class ProjectDto
{
    private int id;

    private String title;

    private String thumbnailPath;

    private Date createDate;

    private Date modifyDate;

    private List<UserProjectDto> users;

    public static ProjectDto mapToProjectDto(Project project)
    {
        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .build();
    }

    public static ProjectDto mapToProjectWithUsersDto(Project project)
    {
        return ProjectDto.builder()
                .id(project.getId())
                .thumbnailPath(project.getThumbnailPath())
                .users(project.getUsers().stream().map(UserProjectDto::mapToProjectUser).toList())
                .build();
    }

    public static ProjectDto mapToProjectDtoFullInfo(Project project)
    {
        return ProjectDto.builder()
                .id(project.getId())
                .title(project.getTitle())
                .thumbnailPath(project.getThumbnailPath())
                .createDate(project.getCreateDate())
                .modifyDate(project.getModifyDate())
                .users(project.getUsers().stream().map(UserProjectDto::mapToProjectUser).toList())
                .build();
    }
}
