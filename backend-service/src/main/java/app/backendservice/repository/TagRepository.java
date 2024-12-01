package app.backendservice.repository;

import app.backendservice.model.Tag;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;
import java.util.Optional;


public interface TagRepository extends JpaRepository<Tag, Integer>
{
    
    Optional<Tag> findById(int id);

    List<Tag> findByFriendlyName(String friendlyName);
    List<Tag> findByIdIn(List<Integer> ids);

    List<Tag> findByFriendlyNameIn(List<String> friendlyName);
    List<Tag> findByTitleIn(Set<String> titles);
    Optional<Tag> findByTitle(String title);

    List<Tag> findAll();
}
