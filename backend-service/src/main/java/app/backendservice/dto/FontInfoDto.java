package app.backendservice.dto;

import org.apache.commons.io.FilenameUtils;

import app.backendservice.model.Font;

import lombok.AllArgsConstructor;
import lombok.Data;



@AllArgsConstructor
@Data
public class FontInfoDto 
{    
    int    id;
    int    fontFamilyId;
    String type;
    String ext; 
   
    public static FontInfoDto mapToFontInfoDto(Font font) 
    {        
        return new FontInfoDto
        (
            font.getId(),
            font.getFontFamily().getId(),
            font.getType(),
            FilenameUtils.getExtension(font.getFilePath())     
        );
    }
}
