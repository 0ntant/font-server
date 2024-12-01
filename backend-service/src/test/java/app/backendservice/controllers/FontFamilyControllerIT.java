package app.backendservice.controllers;

import app.backendservice.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.aspectj.weaver.patterns.ExactTypePattern;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.web.header.Header;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class FontFamilyControllerIT
{
    @Autowired
    MockMvc mockMvc;

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
    void getFontFamilyById_returnsFontFamilyDto() throws Exception
    {
        //given
        var requestBuilder = get("/fontFamily/api/v1/font-family/1")
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
    void getAllFontFamily_returnsListFontsFamilyDto() throws Exception
    {
        //given
        var requestBuilder = get("/fontFamily/api/v1/get-all")
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
    void getFontFamilyInfoById_returnsFontFamilyDto() throws Exception
    {
        //given
        var requestBuilder = get("/fontFamily/api/v1/get-family-info/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectREADME.md
        backend-service/
        docker-compose.yml
        postgresql/
        tools/
All(
                        status().isOk()
                );
    }

    @Test
    void putSaveFontFamilyInfo_returnsFontFamilyDto() throws Exception
    {
        //given
        Path fontPath = Paths.get("./src/test/resources/OpenSans-Light.ttf");
        String fontData = Base64.getEncoder().encodeToString(Files.readAllBytes(fontPath));

        List<SavedTagDto> savedTags = new LinkedList<SavedTagDto>();
        savedTags.add(new SavedTagDto("TestingTag1",1));
        savedTags.add(new SavedTagDto("TestingTag3",2));
        savedTags.add(new SavedTagDto("TestingTag2",3));

        List<SavedFontDto> savedFonts = new LinkedList<SavedFontDto>();
        savedFonts.add(new SavedFontDto(fontData,"Type0", savedTags));

        SavedFontFamilyDto savedFontFamilyDto = new SavedFontFamilyDto("Open Sans", savedFonts);

        var requestBuilder = put("/fontFamily/api/v1/save-font-family")
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(savedFontFamilyDto))
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isCreated()
                );
    }
}
