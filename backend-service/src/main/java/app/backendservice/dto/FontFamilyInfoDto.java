package app.backendservice.dto;

import java.util.List;
import java.util.stream.Collectors;

import app.backendservice.model.FontFamily;
import app.backendservice.model.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class FontFamilyInfoDto 
{
    int    id;
    String title;

    List<TagCatergoryTagsDto> tagCategories;
    List<FontInfoDto>         fonts;

    public static FontFamilyInfoDto mapToFontFamilyInfoDto(FontFamily fontFamily) 
    {
        return new FontFamilyInfoDto
        (
            fontFamily.getId(),
            fontFamily.getTitle(),

            fontFamily.getTags()
                    .stream()     
                    .map(Tag::getTagCategory)
                    .collect(Collectors.toSet())
                    .stream()
                    .map(TagCatergoryTagsDto::mapToTagCatergoryDto)
                    .toList(),

            fontFamily.getFonts()
                    .stream()
                    .map(FontInfoDto::mapToFontInfoDto)
                    .toList()
        );
    }
}
