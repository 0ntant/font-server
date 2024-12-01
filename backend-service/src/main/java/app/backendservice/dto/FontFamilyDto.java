package app.backendservice.dto;

import app.backendservice.model.FontFamily;
import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class FontFamilyDto 
{
    int id;
    String title;

    public static FontFamilyDto mapToFontFamilyDto(FontFamily fontFamily) 
    {
        return new FontFamilyDto
        (
            fontFamily.getId(),
            fontFamily.getTitle()
        );
    }

}
