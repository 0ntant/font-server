package app.backendservice.api.v1;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import app.backendservice.service.TagCategoryService;
import app.backendservice.dto.TagCategoryDto;
import app.backendservice.dto.TagCatergoryTagsDto;
import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping(path="/tagCategory/api/v1",
                produces={"application/json"})
public class TagCategoryController 
{
    private final TagCategoryService tagCategoryService;

    @GetMapping("/get-all")
    public List<TagCategoryDto> getAllTagCategories()
    {   
        return tagCategoryService.findAll();
    }


    @DeleteMapping("/delete-tag-category/{id}")
    public TagCatergoryTagsDto deleteCategory(@PathVariable int id)
    {
        return tagCategoryService.deleteCategory(id);
    }
    
    
    @PutMapping("/save-tag-category")
    @ResponseStatus(HttpStatus.CREATED)
    public TagCategoryDto createTagCategory(@RequestBody TagCategoryDto tagCategoryDto)
    {
        return tagCategoryService.createTagCategory(tagCategoryDto);
    }
}
