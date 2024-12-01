package app.backendservice.repository;

import app.backendservice.model.Font;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FontRepository extends JpaRepository<Font, Integer>
{
    Optional<Font> findById(int id);
    List<Font> findByisCorrupted(boolean corrupted);
    Optional<Font> findByFilePathAndIsCorrupted(String filePath, boolean isCorrupted);
    void delete(Font font);
    List<Font> findAll(); 
}
