package app.backendservice.dto;

import app.backendservice.model.Project;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public class ProjectUserDto
{
    private int id;

    private String title;

    public static ProjectUserDto mapToProjectUserDto(Project project)
    {
        return new ProjectUserDto(
                project.getId(),
                project.getTitle()
        );
    }
}
