package app.backendservice.delete;


import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.model.*;

import app.backendservice.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.header.Header;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class DeleteRequestsIT {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TagRepository tagRepository;

    @Autowired
    FontRepository fontRepository;

    @Autowired
    FontFamilyRepository fontFamilyRepository;

    @Autowired
    ProjectRepository projectRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TagCategoryRepository tagCategoryRepository;

    @Autowired
    UserTokensRepository userTokensRepository;

    static Header authHeader;

    static String username = "username1";

    static String password = "1234";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeEach
    void getAccessTokenForTest() throws Exception
    {
        UserToAuth userToAuth = new UserToAuth(username, password);
        String jsonStringUserToAuth = mapper.writeValueAsString(userToAuth);
        var requestBuilder = post("/auth/api/v1/login")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(jsonStringUserToAuth);

        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        TokensDto tokensDto = mapper.readValue(result.getResponse().getContentAsString(), TokensDto.class);
        String accessToken = tokensDto.getAccessToken().getToken();

        authHeader = new Header("Authorization", String.format("Bearer %s", accessToken));
    }

    @Test
    void handleDeleteFont_returnFontDto() throws Exception
    {
        //given
        int fontIdToDelete = 2;
        Optional<Font> fontToDelete;
        String fontPath = fontRepository.findById(fontIdToDelete).get().getFilePath();
        var requestBuilder = delete(String.format("/font/api/v1/delete-font/%s",fontIdToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
        fontToDelete = fontRepository.findById(fontIdToDelete);

        assertFalse(Files.exists(Path.of(fontPath)));
        assertTrue(fontToDelete.isEmpty());
    }

    @Test
    void handleDeleteFontFamily_returnFontFamilyDto() throws Exception
    {
        //given
        Optional<FontFamily> fontFamilyToDelete;
        int fontFamilyIdToDelete = 2;
        var requestBuilder = delete(String.format("/fontFamily/api/v1/delete-font-family/%s",fontFamilyIdToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isOk()
                );
        fontFamilyToDelete = fontFamilyRepository.findById(fontFamilyIdToDelete);
        assertTrue(fontFamilyToDelete.isEmpty());
    }

    @Test
    @Transactional
    void handleDeleteProject_returnsProjectDto() throws Exception
    {
        //given
        Optional<Project> projectToDelete;
        int projectIdToDelete = 1;
        var requestBuilder = delete(String.format("/project/api/v1/delete-project/%s", projectIdToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
        projectToDelete = projectRepository.findById(projectIdToDelete);
        assertTrue(projectToDelete.isEmpty());
    }

    @Test
    @Transactional
    void handleDeleteTagCategory_returnsTagCategoryDto() throws Exception
    {
        //given
        Optional<TagCategory> tagCategoryToDelete;
        int tagCategoryIdToDelete = 1;
        var requestBuilder = delete(String.format("/tagCategory/api/v1/delete-tag-category/%s",tagCategoryIdToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
        tagCategoryToDelete = tagCategoryRepository.findById(tagCategoryIdToDelete);
        assertTrue(tagCategoryToDelete.isEmpty());
    }

    @Test
    @Transactional
    void handleDeleteTag_returnsTagCategoryDto() throws Exception
    {
        //given
        Optional<Tag> tagToDelete;
        int idTagToDelete = 3;
        var requestBuilder = delete(String.format("/tag/api/v1/delete-tag/%s", idTagToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)

                //expected
                .andExpectAll(
                        status().isOk()
                );
        tagToDelete = tagRepository.findById(idTagToDelete);
        assertTrue(tagToDelete.isEmpty());
    }

    @Test
    @Transactional
    void handleDeleteUser_returnsTagCategoryDto() throws Exception
    {
        //given
        Optional<User> userToDelete;
        Optional<UserTokens> userTokensDeleted;
        int idUserToDelete = 5;
        var requestBuilder = delete(String.format("/user/api/v1/delete-user/%s", idUserToDelete))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)

                //expected
                .andExpectAll(
                        status().isOk()
                );
        userToDelete = userRepository.findById(idUserToDelete);
        userTokensDeleted = userTokensRepository.findById(idUserToDelete);

        assertTrue(userToDelete.isEmpty());
        assertTrue(userTokensDeleted.isEmpty());
    }
}
