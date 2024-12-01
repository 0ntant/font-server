package app.backendservice.repository;

import app.backendservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer>
{
    List<Role> findByIdIn(List<Integer> ids);
    Optional<Role> findByTitle(String title);
}
