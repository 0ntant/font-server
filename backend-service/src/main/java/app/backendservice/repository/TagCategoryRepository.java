package app.backendservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import app.backendservice.model.TagCategory;


public interface TagCategoryRepository extends JpaRepository<TagCategory, Integer>
{
    List<TagCategory> findAll();
    Optional<TagCategory> findById(int id);
    Optional<TagCategory> findByTitle(String title);
}
