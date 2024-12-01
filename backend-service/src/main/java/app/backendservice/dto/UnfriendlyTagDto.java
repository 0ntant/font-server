package app.backendservice.dto;

import app.backendservice.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;



@Data
@AllArgsConstructor
public class UnfriendlyTagDto 
{
    int            id;
    String         title;
    TagCategoryDto category;

    
    public static UnfriendlyTagDto toUnfriendlyTagDto(Tag tag)
    {
        return new UnfriendlyTagDto
        (
            tag.getId(),
            tag.getTitle(),
            TagCategoryDto.mapToTagCategoryDto(tag.getTagCategory())
        );
    }
}
