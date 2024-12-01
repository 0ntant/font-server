package app.backendservice.api.v1;

import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import app.backendservice.dto.DeletedFontFamilyDto;
import app.backendservice.dto.FontFamilyDto;
import app.backendservice.dto.FontFamilyInfoDto;
import app.backendservice.dto.FontFamilyTagFontDto;
import app.backendservice.dto.SavedFontFamilyDto;
import app.backendservice.service.FontFamilyService;
import app.backendservice.service.FontService;

import lombok.RequiredArgsConstructor;


import java.util.List;
import java.util.Optional;



@RestController
@RequiredArgsConstructor
@RequestMapping(path="/fontFamily/api/v1",
                produces={"application/json"})
public class FontFamilyController 
{
    private final FontFamilyService fontFamilyService;
    private final FontService       fontService;

    @GetMapping("/font-family/{id}")
    public Optional<FontFamilyTagFontDto> getFontFamilyById(@PathVariable int id) 
    {               
        fontService.refreshCorruptedStatusAllFonts();
        return fontFamilyService.findById(id);
    }

    @GetMapping("/get-all")
    public List<FontFamilyDto> getFontFamilies()
    {
        return fontFamilyService.findAll();
    }

    @PutMapping(value="/save-font-family")
    @ResponseStatus(HttpStatus.CREATED)
    public FontFamilyTagFontDto saveFontFamily(@Validated @RequestBody SavedFontFamilyDto savedFontFamilyDto) 
    {
        fontService.refreshCorruptedStatusAllFonts();
        return fontFamilyService.saveFontFamily(savedFontFamilyDto);
    }

    @DeleteMapping("/delete-font-family/{id}")
    public DeletedFontFamilyDto deleteFontFamily(@PathVariable int id)
    {
        return fontFamilyService.deleteFontFamily(id);
    }

    @GetMapping("/get-family-info/{id}")
    public FontFamilyInfoDto getFontFamilyInfo(@PathVariable int id)
    {   
        return fontFamilyService.getFontFamilyInfo(id);
    }
}
