package app.backendservice.repository;

import app.backendservice.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer>
{
    List<User> findByIdIn(List<Integer> ids);

    Optional <User> findByEmail(String email);

    Optional <User> findByUsername(String username);

    Optional<User> findByAvatarPath(String avatarPath);
}
