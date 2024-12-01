package app.backendservice.dto;

import app.backendservice.model.FontFamily;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class DeletedFontFamilyDto 
{
    int id;
    String title;
    List<FontDto> fonts;   

    
    public static DeletedFontFamilyDto mapToDeletedFontFamily(FontFamily fontFamily) 
    {
        return new DeletedFontFamilyDto
        (
            fontFamily.getId(),
            fontFamily.getTitle(),
            fontFamily.getFonts()
                .stream()
                .map(FontDto::mapToFontDto)
                .toList()
        );
    }
}
