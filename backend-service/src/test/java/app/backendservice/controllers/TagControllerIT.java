package app.backendservice.controllers;

import app.backendservice.dto.FriendlyTagDto;
import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.model.Tag;
import app.backendservice.repository.TagRepository;
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

import java.util.LinkedList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class TagControllerIT
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    TagRepository tagRepository;

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

        MvcResult result =  mockMvc.perform(requestBuilder).andReturn();
        TokensDto tokensDto = mapper.readValue(result.getResponse().getContentAsString(), TokensDto.class);
        String accessToken = tokensDto.getAccessToken().getToken();

        authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
    }


    @Test
    void handleGetUnfriendly_returnListTagsDto() throws Exception
    {
        //given
        var requestBuilder = get("/tag/api/v1/unfriendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
    }


    @Test
    void handleSetFriendlyNameTags_returnListTagsDto() throws Exception
    {
        //given
        int tagToTestId = 3;
        String friendlyName = "FriendlyName3IT";

        List<FriendlyTagDto> friendlyTagsDto = new LinkedList<>();
        friendlyTagsDto.add(new FriendlyTagDto(tagToTestId, friendlyName));
        String contentBody = mapper.writeValueAsString(friendlyTagsDto);

        var requestBuilder = patch("/tag/api/v1/set-friendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(contentBody)
                .header(authHeader.getName(), authHeader.getValues());

        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
        Tag tagToTest = tagRepository.findById(tagToTestId).get();

        assertNotNull(tagToTest);
        assertEquals(tagToTest.getFriendlyName(), friendlyName);
    }
}
