package app.backendservice.service;

import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import app.backendservice.repository.TagRepository;
import app.backendservice.dto.FriendlyTagDto;
import app.backendservice.dto.TagDto;
import app.backendservice.dto.UnfriendlyTagDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.exception.ResourceNotValidatedException;

import app.backendservice.model.Tag;
import lombok.RequiredArgsConstructor;


@Service
@RequiredArgsConstructor
@Slf4j
public class TagService 
{
    private final TagRepository tagRepository;


    public List<UnfriendlyTagDto> getUnfriendlyTags()
    {
        return tagRepository.findByFriendlyName(null)
                .stream()
                .map(UnfriendlyTagDto::toUnfriendlyTagDto)
                .toList();
    }


    public TagDto delteTag(int id)
    {       
        Tag tagToDelete = tagRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(
                String.format("Tag id=%d not found", 
                id
            )));

        tagRepository.deleteById(tagToDelete.getId());    
        
        return TagDto.mapToTagDto(tagToDelete);
    }
    

    public List<FriendlyTagDto> setFriendlyTagDto(List<FriendlyTagDto> friendlyTagDtos) 
    {
        List<String> friendlyNames = friendlyTagDtos
            .stream()
            .map(FriendlyTagDto::getFriendlyName)
            .toList();
        
        List<Tag> alreadyHasFriendlyNames = tagRepository.findByFriendlyNameIn(friendlyNames);

        alreadyHasFriendlyNames.stream()
            .findAny()
            .ifPresent(alreadyHasFriendlyName -> 
            {
                throw new ResourceNotValidatedException(
                    String.format(
                        "Tag id =%d, title=%s, friendlyName=%s already in database",
                        alreadyHasFriendlyName.getId(), 
                        alreadyHasFriendlyName.getTitle(),
                        alreadyHasFriendlyName.getFriendlyName()
                    )
                );
            });
        
        List<FriendlyTagDto> savedTags = new ArrayList<FriendlyTagDto>();

        List<Integer> ids = friendlyTagDtos
                .stream()
                .map(FriendlyTagDto::getId)
                .toList();
        
        List<Tag> tags = tagRepository.findByIdIn(ids);
                
        tags.stream()
            .findAny()
            .orElseThrow(() -> new ResourceNotFoundException(String.format("Tags not found")));

        tags.forEach(tag -> 
        {
            friendlyTagDtos.stream()
                .filter(friendlyTagDto -> friendlyTagDto.getId().equals(Integer.valueOf(tag.getId())))
                .findFirst()
                .ifPresent(friendlyTagDto -> tag.setFriendlyName(friendlyTagDto.getFriendlyName()));

            try
            {
                savedTags.add(FriendlyTagDto.toFriendlyTagDto(tagRepository.save(tag)));  
            }
            catch(DataIntegrityViolationException ex)
            {
                log.error(ex.getMessage());
            }            
        });  

        return savedTags;
    }   
}
