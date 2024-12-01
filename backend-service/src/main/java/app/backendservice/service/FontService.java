package app.backendservice.service;

import java.util.List;

import org.springframework.stereotype.Service;

import app.backendservice.dto.CorruptedFontDto;
import app.backendservice.dto.FontDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.Font;
import app.backendservice.repository.FontRepository;
import app.backendservice.utils.FileUtils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Service
public class FontService 
{

    private final FontRepository fontRepository;


    public void refreshCorruptedStatusAllFonts()
    {    
            List<Font> fonts = fontRepository.findByisCorrupted(false);   

            fonts.forEach(font ->
            {
                boolean isCorrupted = !(FileUtils.isExists(font.getFilePath()));

                if (isCorrupted)
                {
                    font.setCorrupted(isCorrupted);
                    font.setFontFamily(null);
                    log.warn("Font id={} is corrupted!", font.getId());
                    fontRepository.save(font);
                }      
            });
    }


    public FontDto deleteFont(int id)
    {
        Font fontToDelete = fontRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Font id=%d not found", id
            )));
        fontToDelete.setCorrupted(FileUtils.isExists(fontToDelete.getFilePath()));
        FontDto deletedFontDto = FontDto.mapToFontDtoToDelete(fontToDelete);
        
        fontRepository.deleteById(fontToDelete.getId()); 

        if(FileUtils.isExists(fontToDelete.getFilePath()))
        {
            FileUtils.deleteFile(fontToDelete.getFilePath());
            log.info("Font file with path: {} deleted successfully", fontToDelete.getFilePath());
        }
        else
        {
            log.warn(
            "Font with path: {} not found in file system, font is corrupted: {}", 
                fontToDelete.getFilePath(),
                fontToDelete.isCorrupted()
            );
        }
        
        return deletedFontDto;
    } 


    public List<CorruptedFontDto> getCorruptedFonts()
    {
        return fontRepository.findByisCorrupted(true)
            .stream()
            .map(CorruptedFontDto::toCorruptedFontDto)
            .toList();
    }


    public byte[] getFontBin(int id) 
    { 
        Font font = fontRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Font id=%d not found", id)));
        
        if (font.isCorrupted())
        {
            throw new ResourceNotFoundException(String.format("Font id=%d is corrupted", id));
        }

        return FileUtils.getAllFileToBytes(font.getFilePath());
    }
  
}
