package app.backendservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;

import app.backendservice.model.TagCategory;

import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@NoArgsConstructor
public class TagCategoryDto 
{
    private int    id; 
    private String title;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TagCategoryDto(int id, String title) 
    {
        this.id = id;
        this.title = title;
    }


    public static TagCategoryDto mapToTagCategoryDto(TagCategory tagCategory)
    {
        return new TagCategoryDto(
            tagCategory.getId(),
            tagCategory.getTitle()
        );
    }

    
    public static TagCategory mapToTagCategory(TagCategoryDto tagCategoryDto)
    {
        return TagCategory.builder()
            .title(tagCategoryDto.getTitle())
            .build();
    }        
}
