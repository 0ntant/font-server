package app.backendservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.backendservice.repository.TagCategoryRepository;
import jakarta.transaction.Transactional;
import app.backendservice.dto.TagCategoryDto;
import app.backendservice.dto.TagCatergoryTagsDto;
import app.backendservice.exception.ResourceNotValidatedException;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.TagCategory;

import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@Service
public class TagCategoryService
{
    public final TagCategoryRepository tagCategoryRepository;

    public TagCategoryDto createTagCategory(TagCategoryDto tagCategoryDto)
    {
        TagCategory savedTagCategory = new TagCategory(); 

        tagCategoryRepository.findByTitle(tagCategoryDto.getTitle())
            .ifPresent(value -> 
            { 
                throw new ResourceNotValidatedException(
                    String.format(
                        "TagCategory id=%d title=%s already exists", 
                        value.getId(), 
                        value.getTitle()
                    )
                );
            });

        savedTagCategory = tagCategoryRepository.save(TagCategoryDto.mapToTagCategory(tagCategoryDto));

        return TagCategoryDto.mapToTagCategoryDto(savedTagCategory);
    }


    @Transactional
    public TagCatergoryTagsDto deleteCategory(int id)
    {
        TagCategory tagCategoryToDelete = tagCategoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("TagCategory id=%d not found", id
            )));

        tagCategoryRepository.deleteById(tagCategoryToDelete.getId());    
        
        return TagCatergoryTagsDto.mapToTagCatergoryDto(tagCategoryToDelete);
    } 


    public List<TagCategoryDto>findAll()
    {   
        List<TagCategoryDto> tagCategories = tagCategoryRepository.findAll()
            .stream()
            .map(TagCategoryDto::mapToTagCategoryDto)
            .toList();

        return tagCategories;
    }
}
