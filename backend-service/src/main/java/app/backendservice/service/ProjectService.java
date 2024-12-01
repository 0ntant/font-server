package app.backendservice.service;

import app.backendservice.dto.ProjectDto;
import app.backendservice.dto.UserProjectDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.Project;
import app.backendservice.model.User;
import app.backendservice.repository.ProjectRepository;
import app.backendservice.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Date;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProjectService
{
    final private ProjectRepository projectRepository;
    final private UserRepository userRepository;

    public List<ProjectDto> getAll() {
        return projectRepository.findAll()
                .stream()
                .map(ProjectDto::mapToProjectDto)
                .toList();
    }

    private ProjectDto getProjectWithUsers(Project project)
    {
        return ProjectDto.mapToProjectWithUsersDto(project);
    }

    private ProjectDto getProjectFullInfo(Project project)
    {
        return ProjectDto.mapToProjectDtoFullInfo(project);
    }

    private Project findById(int id)
    {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Project id=%s not found",id)));
    }

    public ProjectDto getInfo(int id)
    {
        return this.getProjectFullInfo(this.findById(id));
    }


    private User findUserById(int id)
    {
        return userRepository.findById(id)
                .orElseThrow(()-> new ResourceNotFoundException(String.format("User with id=%s not found", id)));
    }

    private List<User> findUsersByIds(List<Integer> ids)
    {
        List<User> foundUsers = userRepository.findByIdIn(ids);
        if (foundUsers.isEmpty() && !ids.isEmpty())
        {
            throw new ResourceNotFoundException(String.format("Users with ids: %s not found", ids));
        }
        if (foundUsers.size() != ids.size())
        {
            List<Integer> foundIds = foundUsers
                    .stream()
                    .map(User::getId)
                    .toList();
            throw new ResourceNotFoundException(String.format("Users with ids: %s not found", ids.removeAll(foundIds)));
        }

        return foundUsers;
    }

    @Transactional
    public ProjectDto deleteById(int id)
    {
        Project project = this.findById(id);
        projectRepository.deleteById(id);

        return this.getProjectWithUsers(project);
    }

    @Transactional
    public ProjectDto editProject(ProjectDto projectDto)
    {
        Project projectToSave = this.findById(projectDto.getId());
        List<Integer> usersIds = projectDto.getUsers()
                .stream()
                .map(UserProjectDto::getId)
                .toList();
        List<User> usersInProject = this.findUsersByIds(usersIds);

        projectToSave.setUsers(usersInProject);
        projectToSave.setTitle(projectDto.getTitle());
        projectToSave.setThumbnailPath(projectDto.getThumbnailPath());
        projectToSave.setModifyDate(Date.from(Instant.now()));

        try
        {
            projectRepository.save(projectToSave);
        }
        catch (Exception ex)
        {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        return this.getProjectWithUsers(projectToSave);
    }

    @Transactional
    public ProjectDto addProject(ProjectDto projectDto)
    {
        Project projectToSave = new Project();

        projectToSave.setModifyDate(Date.from(Instant.now()));
        projectToSave.setCreateDate(Date.from(Instant.now()));
        projectRepository.save(projectToSave);

        List<Integer> usersIds = projectDto.getUsers()
                .stream()
                .map(UserProjectDto::getId)
                .toList();

        List<User> usersInProject = this.findUsersByIds(usersIds);

        projectToSave.setUsers(usersInProject);
        projectToSave.setTitle(projectDto.getTitle());
        projectToSave.setThumbnailPath(projectDto.getThumbnailPath());

        try
        {
            projectRepository.save(projectToSave);
        }
        catch (Exception ex)
        {
            throw new ResourceNotFoundException(ex.getMessage());
        }

        return this.getProjectWithUsers(projectToSave);
    }
}
