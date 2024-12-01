package app.backendservice.repository;

import app.backendservice.model.FontFamily;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface FontFamilyRepository extends JpaRepository<FontFamily, Integer> 
{

    List<FontFamily> findAll();

    Optional<FontFamily> findById(int id);
    Optional<FontFamily> findByTitle(String title);
}
