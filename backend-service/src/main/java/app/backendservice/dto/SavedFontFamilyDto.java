package app.backendservice.dto;

import java.util.List;
import java.util.ArrayList;

import app.backendservice.model.FontFamily;
import app.backendservice.model.Tag;
import app.backendservice.model.Font;

import lombok.AllArgsConstructor;
import lombok.Data;



@AllArgsConstructor
@Data
public class SavedFontFamilyDto 
{
    String             title;
    List<SavedFontDto> fonts;

    public static FontFamily mapToFontFamilyOnlyTitle(SavedFontFamilyDto savedFontFamilyDto)
    {
        return FontFamily.builder()
            .title(savedFontFamilyDto.getTitle())
            .tags(new ArrayList<Tag>())
            .fonts(new ArrayList<Font>())
            .build();
    }
}
