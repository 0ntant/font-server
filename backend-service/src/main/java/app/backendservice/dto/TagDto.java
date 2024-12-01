package app.backendservice.dto;

import app.backendservice.model.Tag;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class TagDto 
{
    int    id;
    String title;
    String friendlyName;

    
    public static TagDto mapToTagDto(Tag tag) 
    {
        return new TagDto
        (
            tag.getId(),
            tag.getTitle(),
            tag.getFriendlyName()
        );
    }

}
