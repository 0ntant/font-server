package app.backendservice.dto;

import java.util.List;

import app.backendservice.model.TagCategory;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class TagCatergoryTagsDto 
{

    private int          id;
    private String       title;
    private List<TagDto> tags;


    public static TagCatergoryTagsDto mapToTagCatergoryDto(TagCategory tagCategory)
    {
        return new TagCatergoryTagsDto(
            tagCategory.getId(),
            tagCategory.getTitle(),
            tagCategory.getTags()
                        .stream()
                        .map(TagDto::mapToTagDto)
                        .toList()
        );
    }
}
