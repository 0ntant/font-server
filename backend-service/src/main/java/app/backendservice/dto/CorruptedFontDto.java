package app.backendservice.dto;

import app.backendservice.model.Font;
import lombok.AllArgsConstructor;
import lombok.Data;


@Data
@AllArgsConstructor
public class CorruptedFontDto 
{
    int id;
    String type;
    String filePath;

    
    public static CorruptedFontDto toCorruptedFontDto(Font font)
    {
        return new CorruptedFontDto
        (
            font.getId(),
            font.getType(),
            font.getFilePath()
        );
    }
}
