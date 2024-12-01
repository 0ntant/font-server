package app.backendservice.service;

import java.util.Optional;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashSet;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.file.PathUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import app.backendservice.dto.DeletedFontFamilyDto;
import app.backendservice.dto.FontFamilyDto;
import app.backendservice.dto.FontFamilyInfoDto;
import app.backendservice.dto.FontFamilyTagFontDto;
import app.backendservice.dto.SavedFontDto;
import app.backendservice.dto.SavedFontFamilyDto;
import app.backendservice.dto.SavedTagDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.FontFamily;
import app.backendservice.model.Tag;
import app.backendservice.model.TagCategory;
import app.backendservice.model.Font;
import app.backendservice.repository.FontFamilyRepository;
import app.backendservice.repository.FontRepository;
import app.backendservice.repository.TagCategoryRepository;
import app.backendservice.repository.TagRepository;
import app.backendservice.exception.ResourceNotValidatedException;
import app.backendservice.utils.FileUtils;
import app.backendservice.utils.TTFParserApacheUtils;
import app.backendservice.config.DirConfiguration;
import app.backendservice.utils.FontConverterUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@RequiredArgsConstructor
@Service
public class FontFamilyService 
{
    private final FontFamilyRepository fontFamilyRepository;
    private final TagRepository tagRepository;
    private final FontRepository fontRepository;
    private final TagCategoryRepository tagCategoryRepository;
    private final DirConfiguration dirConfiguration;
    private final FontService fontService;

    public FontFamilyInfoDto getFontFamilyInfo(int id)
    {
        FontFamily fontFamily = fontFamilyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("FontFamily id=%d not found", id
            )));

        return FontFamilyInfoDto.mapToFontFamilyInfoDto(fontFamily);       
    }

    @Transactional
    public DeletedFontFamilyDto deleteFontFamily(int id)
    {       
        FontFamily fontFamilyToDelete = fontFamilyRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("FontFamily id=%d not found", id
            )));

        DeletedFontFamilyDto deletedFontFamilyDto = DeletedFontFamilyDto.mapToDeletedFontFamily(fontFamilyToDelete);

        deletedFontFamilyDto.getFonts().forEach(fontDto -> {fontService.deleteFont(fontDto.getId());});      
        fontFamilyRepository.deleteById(fontFamilyToDelete.getId());    
        
        return deletedFontFamilyDto;
    }    


    private Set<Tag> saveTags (List<SavedTagDto> savedTagsDto)
    {
        Set<Tag> savedTags = new HashSet<Tag>();

        savedTagsDto.forEach(savedTagDto ->
        {
            savedTags.add(saveTag(savedTagDto));
        });
        
        return savedTags; 
    }


    private Tag saveTag (SavedTagDto savedTagDto)
    {
        Tag                    savedTag    = new Tag();  
        Optional <TagCategory> tagCategory = tagCategoryRepository.findById(savedTagDto.getCategoryId());
        
        tagCategory.orElseThrow(() -> new ResourceNotFoundException(
            String.format("Tag category id=%d not found", savedTagDto.getCategoryId())
        ));

        return tagRepository.findByTitle(savedTagDto.getTitle())
            .orElseGet(() ->
            {
                savedTag.setTagCategory(tagCategory.get());
                savedTag.setTitle(savedTagDto.getTitle());
                savedTag.setFriendlyName(null);

                return tagRepository.save(savedTag);
            });      
    }
    

    private List<Font> saveFonts (List<SavedFontDto> savedFontsDto)
    {
        List<Font> savedFonts = new ArrayList<Font>();

        savedFontsDto.forEach(savedFontDto -> 
        {
            savedFonts.add(saveFont(savedFontDto));
        });

        return savedFonts;
    }


    private Font saveFont (SavedFontDto savedFontDto)
    {
        Font   savedFont = null;
        String savedDirTemplateTTF; 
        String savedDirTemplateWoff2;
        String savedDirTemplate;
        byte[] fontBytesBuffer;

        TTFParserApacheUtils savedTTF;

        try
        {
            savedTTF = new TTFParserApacheUtils(Base64.getDecoder().decode(savedFontDto.getFontFile()), dirConfiguration);
        }
        catch(IllegalArgumentException ex)
        {
            throw new ResourceNotValidatedException(String.format(
                "Can't decode from base64 from array type=%s", 
                savedFontDto.getType()
            ));
        }  

        savedDirTemplateTTF   = String.format("%s%s.ttf",   dirConfiguration.getFont(), savedTTF.getTitle());
        savedDirTemplateWoff2 = String.format("%s%s.woff2", dirConfiguration.getFont(), savedTTF.getTitle());
      
        fontRepository.findByFilePathAndIsCorrupted(savedDirTemplateTTF, false)
            .ifPresent(value -> 
            {
                throw new ResourceNotValidatedException(
                        String.format(
                                "Font: %s id=%d already exists and not corrupted",
                                FilenameUtils.getName(value.getFilePath()),
                                value.getId())
                );
            });

        fontRepository.findByFilePathAndIsCorrupted(savedDirTemplateWoff2, false)
            .ifPresent(value -> 
            {
                throw new ResourceNotValidatedException(
                    String.format(
                            "Font: %s id=%d already exists and not corrupted",
                            FilenameUtils.getName(value.getFilePath()),
                            value.getId())
                );
            });

        try 
        {
            fontBytesBuffer = FontConverterUtil.ttfToWoff2(savedTTF.getFileBytes(), dirConfiguration);
            savedFont       = fontRepository.findByFilePathAndIsCorrupted(savedDirTemplateWoff2, true).orElseGet(Font::new);
            savedFont.setFilePath(savedDirTemplateWoff2);
            savedDirTemplate = savedDirTemplateWoff2;
        }
        catch (Exception ex)
        {
            log.warn(
                "Can't convert: {} FontFamily: {} to woff2, will be save as ttf" , 
                savedTTF.getTitle(), 
                savedTTF.getFontFamily()
            );
        
            savedFont = fontRepository.findByFilePathAndIsCorrupted(savedDirTemplateTTF, true).orElseGet(Font::new);
            savedFont.setFilePath(savedDirTemplateTTF);

            fontBytesBuffer  = savedTTF.getFileBytes();
            savedDirTemplate = savedDirTemplateTTF;
        }      
        
        savedFont.setType(savedFontDto.getType());      
        savedFont.setCorrupted(false);
        savedFont.setFontFamily(null);
        
        savedFont = fontRepository.save(savedFont);

        FileUtils.saveFile(fontBytesBuffer, savedDirTemplate);

        return savedFont;
    }


    @Transactional
    public FontFamilyTagFontDto saveFontFamily(SavedFontFamilyDto savedFontFamilyDto) 
    {
        FontFamily savedFontFamily = new FontFamily();       
        List<Font> savedFonts      = new ArrayList<Font>();
        Set<Tag>   savedTags       = new HashSet<Tag>();

        savedFontFamily = fontFamilyRepository.findByTitle(savedFontFamilyDto.getTitle())
            .orElseGet(() -> SavedFontFamilyDto.mapToFontFamilyOnlyTitle(savedFontFamilyDto));  
      
        savedFontFamilyDto.getFonts()
            .forEach(savedFontDto -> savedTags.addAll(saveTags(savedFontDto.getTags())));
      
        savedFonts = saveFonts(savedFontFamilyDto.getFonts());             
        
        for (Tag savedTag : savedTags)
        {
            savedFontFamily.addTag(savedTag);
        }

        for (Font savedFont : savedFonts)
        {
            savedFont.setFontFamily(savedFontFamily);
            savedFontFamily.addFont(savedFont);
        }

        return FontFamilyTagFontDto.mapToFontFamilyTagFontDto(fontFamilyRepository.save(savedFontFamily));
    }


    public Optional<FontFamilyTagFontDto> findById(int id) 
    {  
        Optional<FontFamily> fontFamilyOptional = fontFamilyRepository.findById(id);
        fontFamilyOptional.orElseThrow(() -> new ResourceNotFoundException(String.format("FontFamily not found id=%d",id)));     

        return fontFamilyOptional.map(FontFamilyTagFontDto::mapToFontFamilyTagFontDto);       
    }


    public List<FontFamilyDto> findAll() 
    {
        return fontFamilyRepository.findAll()
            .stream()
            .map(FontFamilyDto::mapToFontFamilyDto)
            .toList();
    }
}
