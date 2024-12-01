package app.backendservice.dto;

import app.backendservice.model.FontFamily;
import app.backendservice.utils.FileUtils;
import app.backendservice.model.Font;

import java.util.Base64;
import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;



@AllArgsConstructor
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FontDto 
{
    int id;

    String type;
    String fontFile;
    int fontFamilyId;
    boolean isCorrupted;

    public FontDto(@JsonProperty("id") int id)
    {
        this.id = id; 
    }

    
    public static FontDto mapToFontDto(Font font) 
    {
        return  FontDto.builder()
                .id(font.getId())
                .type(font.getType())
                .isCorrupted(font.isCorrupted())
                .build();
    }

    public static FontDto mapToFontDtoToDelete(Font font)
    {
        int fontFamilyId = Optional.ofNullable(font.getFontFamily())
                .map(FontFamily::getId)
                .orElse(0);

        return  FontDto.builder()
                .id(font.getId())
                .type(font.getType())
                .fontFamilyId(fontFamilyId)
                .isCorrupted(font.isCorrupted())
                .build();
    }
}
