package app.backendservice.repository;

import app.backendservice.model.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface ProjectRepository extends JpaRepository<Project, Integer>
{
    List<Project> findAll();

    @Override
    Optional<Project> findById(Integer integer);

    List<Project> findByIdIn(List<Integer> ids);
}
