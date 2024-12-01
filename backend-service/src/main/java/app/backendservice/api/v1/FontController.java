package app.backendservice.api.v1;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.backendservice.dto.CorruptedFontDto;
import app.backendservice.dto.FontDto;
import app.backendservice.service.FontService;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@RestController
@RequiredArgsConstructor
@RequestMapping(path="/font/api/v1",
                produces={"application/json"})
public class FontController 
{
    private final FontService fontService;

    @GetMapping(value="/corrupted-fonts")
    public List<CorruptedFontDto> getCorruptedFonts() 
    {
        fontService.refreshCorruptedStatusAllFonts();
        return fontService.getCorruptedFonts();
    }

    @DeleteMapping("/delete-font/{id}")
    public FontDto deleteTagCategory(@PathVariable int id)
    {
        return fontService.deleteFont(id);
    } 

    @GetMapping("/get-font-bin/{id}")
    public byte[] getFontBin(@PathVariable int id)
    {   
        return fontService.getFontBin(id);
    }

}
