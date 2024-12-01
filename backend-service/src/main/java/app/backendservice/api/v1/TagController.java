package app.backendservice.api.v1;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import app.backendservice.dto.FriendlyTagDto;
import app.backendservice.dto.TagDto;
import app.backendservice.dto.UnfriendlyTagDto;
import app.backendservice.service.TagService;
import lombok.RequiredArgsConstructor;



@RestController
@RequiredArgsConstructor
@RequestMapping(path="/tag/api/v1",
                produces={"application/json"})
public class TagController 
{
    private final TagService tagService;

    @GetMapping("/unfriendly-tags")
    public List<UnfriendlyTagDto> getUnfriendlyTags()
    {
       return tagService.getUnfriendlyTags();
    }

    @PatchMapping("/set-friendly-tags")
    public List<FriendlyTagDto> setFriendlyTags(@RequestBody List<FriendlyTagDto> friendlyTagsDto)
    {
        return tagService.setFriendlyTagDto(friendlyTagsDto);
    }

    @DeleteMapping("/delete-tag/{id}")
    public TagDto deleteTag(@PathVariable int id)
    {
        return tagService.delteTag(id);
    }
}
