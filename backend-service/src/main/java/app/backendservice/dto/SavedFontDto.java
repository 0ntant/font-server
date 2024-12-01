package app.backendservice.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;


@AllArgsConstructor
@Data
public class SavedFontDto 
{
    String fontFile;
    String type;

    List<SavedTagDto> tags;
}
