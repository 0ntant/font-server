package app.backendservice.controllers;

import app.backendservice.dto.TokenDto;
import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.model.User;
import app.backendservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class AuthControllerIT
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    static String accessToken;

    static String refreshToken;

    static String username = "username1";

    static String password = "1234";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Test
    @BeforeEach
    void handleLoginUser_ReturnsAccessAndRefreshTokens() throws Exception
    {
        // given
        UserToAuth userToAuth = new UserToAuth(username, password);
        String jsonString = mapper.writeValueAsString(userToAuth);
        var requestBuilder = post("/auth/api/v1/login")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        //then
        MvcResult result = this.mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON)
                )
                .andReturn();

        //get tokens
        String content = result.getResponse().getContentAsString();
        TokensDto tokensDto = mapper.readValue(content, TokensDto.class);

        accessToken = tokensDto.getAccessToken().getToken();
        refreshToken = tokensDto.getRefreshToken().getToken();
    }


    @Test
    void handleLoginUser_ReturnsBadCredentials() throws Exception
    {
        // given
        UserToAuth userToAuth = new UserToAuth("username1", "WrongPassword");
        String jsonString = mapper.writeValueAsString(userToAuth);
        var requestBuilder = post("/auth/api/v1/login")
                .content(jsonString)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isUnauthorized(),
                        content().contentType(MediaType.APPLICATION_JSON)
                );
   }

   @Test
   void handleRefreshToken_ReturnsAccessToken() throws  Exception
   {
       //given
       var requestBuilder = get("/auth/api/v1/refresh")
               .header("Authorization", String.format("Bearer %s",refreshToken));
       //then
       MvcResult result = mockMvc.perform(requestBuilder)
       //expected
               .andExpectAll(
                       status().isOk()
               ).andReturn();
       String content = result.getResponse().getContentAsString();
       mapper.readValue(content, TokenDto.class);
   }


   @Test
   void  handleLogoutUser_ReturnsSuccessLogoutString() throws  Exception
   {
       //given
       var requestBuilder = get("/auth/api/v1/logout")
               .header("Authorization", String.format("Bearer %s",refreshToken));
       //then
       mockMvc.perform(requestBuilder)
       //expected
               .andExpectAll(
                       status().isOk()
               );
       User logoutUser = userRepository.findByUsername(username).get();
       assertNotNull(logoutUser);
       assertNotNull(logoutUser.getUserTokens());
       assertNull(logoutUser.getUserTokens().getJwtRefresh());
   }
}
