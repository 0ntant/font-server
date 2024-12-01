package app.backendservice.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import app.backendservice.model.FontFamily;
import lombok.AllArgsConstructor;
import lombok.Data;



@AllArgsConstructor
@Data
public class FontFamilyTagFontDto 
{
    int id;
    String title;

    List<TagCatergoryTagsDto> tagCategories;
    List<FontDto> fonts;


    @JsonCreator
    public FontFamilyTagFontDto(@JsonProperty("id") int id) 
    {
        this.id = id;
    }


    public static FontFamilyTagFontDto mapToFontFamilyTagFontDto(FontFamily fontFamily) 
    {
        return new FontFamilyTagFontDto
        (
            fontFamily.getId(),
            fontFamily.getTitle(),

            fontFamily.getTags()
                    .stream()     
                    .map(tag -> tag.getTagCategory())                     
                    .collect(Collectors.toSet())
                    .stream()
                    .map(TagCatergoryTagsDto::mapToTagCatergoryDto)
                    .toList(),

            fontFamily.getFonts()
                    .stream()
                    .map(FontDto::mapToFontDto)
                    .toList()
        );
    }
}
