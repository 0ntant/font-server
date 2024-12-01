package app.backendservice.repository;

import app.backendservice.model.UserTokens;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserTokensRepository extends JpaRepository<UserTokens, Integer>
{
    Optional<UserTokens> findByJwtRefresh(String jwtRefresh);
}
