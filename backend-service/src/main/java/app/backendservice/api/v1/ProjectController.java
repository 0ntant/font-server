package app.backendservice.api.v1;

import app.backendservice.dto.ProjectDto;
import app.backendservice.service.ProjectService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/project/api/v1",
        produces={"application/json"})
@RequiredArgsConstructor
public class ProjectController
{
    final private ProjectService projectService;

    @GetMapping("get-all")
    public List<ProjectDto> getAll()
    {
        return projectService.getAll();
    }

    @PostMapping("create-project")
    @ResponseStatus(HttpStatus.CREATED)
    public ProjectDto creteProject(@Valid @RequestBody ProjectDto project)
    {
        return projectService.addProject(project);
    }

    @DeleteMapping("delete-project/{id}")
    public ProjectDto deleteProject(@PathVariable int id)
    {
        return projectService.deleteById(id);
    }

    @PutMapping("edit-project")
    public ProjectDto editProject(@Valid  @RequestBody ProjectDto projectDto)
    {
        return projectService.editProject(projectDto);
    }

    @GetMapping("get/{id}")
    public ProjectDto get(@PathVariable int id)
    {
        return projectService.getInfo(id);
    }


}
