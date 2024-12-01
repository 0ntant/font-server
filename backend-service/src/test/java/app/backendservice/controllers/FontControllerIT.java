package app.backendservice.controllers;


import app.backendservice.dto.*;
import app.backendservice.model.Font;
import app.backendservice.model.FontFamily;
import app.backendservice.repository.FontRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.parameters.P;
import org.springframework.security.web.header.Header;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.util.Base64;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
@Transactional
public class FontControllerIT
{
    @Autowired
    MockMvc mockMvc;
    static Header authHeader;

    static String username = "username1";

    static String password = "1234";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    //cp ./src/test/resources/OpenSans-Bold.ttf  ./src/main/resources/fonts
    @BeforeAll
    static void preparationDataForTest() throws IOException
    {
        Path fontTestFile1 = Paths.get("./src/test/resources/OpenSans-Bold.ttf");
        Path fontSourceFile1 = Paths.get("./src/main/resources/fonts/OpenSans-Bold.ttf");

        if (!Files.exists(fontSourceFile1))
        {
            Files.copy(fontTestFile1, fontSourceFile1);
        }
    }

    @BeforeEach
    void  getAccessTokenForTest() throws Exception
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
    void handleGetCorruptedFonts_returnsListFontsDto() throws Exception
    {
        //given
        var requestBuilder = get("/font/api/v1/corrupted-fonts")
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
    void handleGetForm_returnFormHtml() throws Exception
    {
        //given
        var requestBuilder = get("/font/form")
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
    void handleGetFontBin_returnFontsBytes() throws Exception
    {
        //given
        var requestBuilder = get("/font/api/v1/get-font-bin/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
    }
}
