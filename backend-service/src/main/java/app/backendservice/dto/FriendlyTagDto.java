package app.backendservice.dto;

import app.backendservice.model.Tag;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class FriendlyTagDto 
{
    Integer id;
    String  friendlyName;
    
    
    public static FriendlyTagDto toFriendlyTagDto(Tag tag)
    {
        return new FriendlyTagDto
        (
            tag.getId(),
            tag.getFriendlyName()
        );
    }
}
