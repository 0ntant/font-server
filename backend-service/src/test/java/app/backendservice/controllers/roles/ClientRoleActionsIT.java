package app.backendservice.controllers.roles;

import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserSafeDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.model.User;
import app.backendservice.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ClientRoleActionsIT
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    static String accessToken;

    static String refreshToken;

    static  int clientId = 2;

    static String clientUsername = "username2";

    static String password = "1234";

    ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

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

    @Test
    @BeforeEach
    void handleLoginUser_ReturnsAccessAndRefreshTokens() throws Exception
    {
        // given
        UserToAuth userToAuth = new UserToAuth(clientUsername, password);
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

        assertEquals(userRepository.findByUsername(clientUsername).get().getId(), clientId);

        accessToken = tokensDto.getAccessToken().getToken();
        refreshToken = tokensDto.getRefreshToken().getToken();
    }

    @Test
    void tryToEscalateRoleUsingAccessToken_returnsAccessDenied() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestManagerBuilder = put(String.format("/role/api/v1/set-role-manager/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestAdminBuilder = put(String.format("/role/api/v1/set-role-admin/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeveloperBuilder = put(String.format("/role/api/v1/set-role-developer/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerResult = mockMvc.perform(requestManagerBuilder);
        var adminResult = mockMvc.perform(requestAdminBuilder);
        var developerResult = mockMvc.perform(requestDeveloperBuilder);
        //expected
        managerResult.andExpect(status().isForbidden());
        adminResult.andExpect(status().isForbidden());
        developerResult.andExpect(status().isForbidden());
    }

    @Test
    void tryToEscalateRoleUsingRefreshToken_returnsIsUnauthorized() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestManagerBuilder = put(String.format("/role/api/v1/set-role-manager/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestAdminBuilder = put(String.format("/role/api/v1/set-role-admin/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeveloperBuilder = put(String.format("/role/api/v1/set-role-developer/%s",clientId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerResult = mockMvc.perform(requestManagerBuilder);
        var adminResult = mockMvc.perform(requestAdminBuilder);
        var developerResult = mockMvc.perform(requestDeveloperBuilder);
        //expected
        managerResult.andExpect(status().isUnauthorized());
        adminResult.andExpect(status().isUnauthorized());
        developerResult.andExpect(status().isUnauthorized());
    }

    @Test
    void tagControllerUsingAccessToken_returnsIsOk() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestManagerBuilder = get("/tag/api/v1/unfriendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestManagerBuilder)
        //expected
                .andExpect(status().isOk());


    }

    @Test
    void tagControllerUsingAccessToken_returnsAccessDenied() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestSetFriendlyTagNameManagerBuilder = patch("/tag/api/v1/set-friendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteTagManagerBuilder = delete("/tag/api/v1/delete-tag/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSetFriendlyTagNameResult =  mockMvc.perform(requestSetFriendlyTagNameManagerBuilder);
        var managerDeleteTagResult =  mockMvc.perform(requestDeleteTagManagerBuilder);
        //expected
        managerSetFriendlyTagNameResult.andExpect(status().isForbidden());
        managerDeleteTagResult.andExpect(status().isForbidden());
    }

    @Test
    void tagControllerUsingRefreshToken_returnsIsUnauthorized() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestSetFriendlyTagNameManagerBuilder = patch("/tag/api/v1/set-friendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteTagManagerBuilder = delete("/tag/api/v1/delete-tag/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSetFriendlyTagNameResult =  mockMvc.perform(requestSetFriendlyTagNameManagerBuilder);
        var managerDeleteTagResult =  mockMvc.perform(requestDeleteTagManagerBuilder);
        //expected
        managerSetFriendlyTagNameResult.andExpect(status().isUnauthorized());
        managerDeleteTagResult.andExpect(status().isUnauthorized());
    }


    @Test
    void tagCategoryControllerUsingAccessToken_returnsIsOk() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestManagerBuilder = get("/tagCategory/api/v1/get-all")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestManagerBuilder)
        //expected
                .andExpect(status().isOk());
    }

    @Test
    void tagCategoryControllerUsingAccessToken_returnsAccessDenied() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestSaveCategoryTagNameManagerBuilder = patch("/tag/api/v1/set-friendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteTagCategoryManagerBuilder = delete("/api/v1/delete-tag-category/1")
                .accept(MediaType.APPLICATION_JSON).header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSaveCategoryTagResult =  mockMvc.perform(requestSaveCategoryTagNameManagerBuilder);
        var managerDeleteTagCategoryResult =  mockMvc.perform(requestDeleteTagCategoryManagerBuilder);
        //expected
        managerSaveCategoryTagResult.andExpect(status().isForbidden());
        managerDeleteTagCategoryResult.andExpect(status().isForbidden());

    }

    @Test
    void tagCategoryControllerUsingRefreshToken_returnsIsUnauthorized() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestSaveCategoryTagNameManagerBuilder = patch("/tag/api/v1/set-friendly-tags")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteTagCategoryManagerBuilder = delete("/api/v1/delete-tag-category/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSaveCategoryTagResult =  mockMvc.perform(requestSaveCategoryTagNameManagerBuilder);
        var managerDeleteTagCategoryResult =  mockMvc.perform(requestDeleteTagCategoryManagerBuilder);
        //expected
        managerSaveCategoryTagResult.andExpect(status().isUnauthorized());
        managerDeleteTagCategoryResult.andExpect(status().isUnauthorized());
    }

    @Test
    void fontControllerUsingAccessToken_returnsIsOK() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestGetCorruptedFontsManagerBuilder = get("/font/api/v1/corrupted-fonts")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetFontByIdManagerBuilder = get("/font/api/v1/get-font-bin/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerGetCorruptedFontsTagResult =  mockMvc.perform(requestGetCorruptedFontsManagerBuilder);
        var managerGetFontByIdResult =  mockMvc.perform(requestGetFontByIdManagerBuilder);
        //expected
        managerGetCorruptedFontsTagResult.andExpect(status().isOk());
        managerGetFontByIdResult.andExpect(status().isOk());
    }

    @Test
    void fontControllerUsingAccessToken_returnsAccessDenied() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestGetFontUploadFormManagerBuilder = get("/font/form")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteFontByIdManagerBuilder = delete("/font/api/v1/delete-font/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerGetFontUploadFormResult =  mockMvc.perform(requestGetFontUploadFormManagerBuilder);
        var managerDeleteFontByIdResult =  mockMvc.perform(requestDeleteFontByIdManagerBuilder);
        //expected
        managerGetFontUploadFormResult.andExpect(status().isForbidden());
        managerDeleteFontByIdResult.andExpect(status().isForbidden());

    }

    @Test
    void fontControllerUsingRefreshToken_returnsIsUnauthorized () throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestGetFontUploadFormManagerBuilder = get("/font/form")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteFontByIdManagerBuilder = delete("/font/api/v1/delete-font/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerGetFontUploadFormResult =  mockMvc.perform(requestGetFontUploadFormManagerBuilder);
        var managerDeleteFontByIdResult =  mockMvc.perform(requestDeleteFontByIdManagerBuilder);
        //expected
        managerGetFontUploadFormResult.andExpect(status().isUnauthorized());
        managerDeleteFontByIdResult.andExpect(status().isUnauthorized());
    }

    @Test
    void fontFamilyControllerUsingAccessToken_returnsIsOk() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestGetFontFamilyManagerBuilder = get("/fontFamily/api/v1/font-family/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetAllFontFamiliesIdManagerBuilder = get("/fontFamily/api/v1/get-all")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetFontInfoByIdManagerBuilder = get("/fontFamily/api/v1/get-family-info/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerGetFontFamilyResult =  mockMvc.perform(requestGetFontFamilyManagerBuilder);
        var managerGetAllFontFamiliesResult =  mockMvc.perform(requestGetAllFontFamiliesIdManagerBuilder);
        var managerGetFontInfoByIdResult =  mockMvc.perform(requestGetFontInfoByIdManagerBuilder);
        //expected
        managerGetFontFamilyResult.andExpect(status().isOk());
        managerGetAllFontFamiliesResult.andExpect(status().isOk());
        managerGetFontInfoByIdResult.andExpect(status().isOk());
    }

    @Test
    void fontFamilyControllerUsingAccessToken_returnsAccessDenied() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestSaveFontFamilyManagerBuilder = put("/fontFamily/api/v1/save-font-family")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteFontFamilyManagerBuilder = delete("/fontFamily/api/v1/delete-font-family/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSaveFontFamilyResult =  mockMvc.perform(requestSaveFontFamilyManagerBuilder);
        var managerDeleteFontFamilyResult =  mockMvc.perform(requestDeleteFontFamilyManagerBuilder);
        //expected
        managerSaveFontFamilyResult.andExpect(status().isForbidden());
        managerDeleteFontFamilyResult.andExpect(status().isForbidden());

    }


    @Test
    void fontFamilyControllerUsingRefreshToken_returnsIsUnauthorized() throws Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestSaveFontFamilyManagerBuilder = put("/fontFamily/api/v1/save-font-family")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteFontFamilyManagerBuilder = delete("/fontFamily/api/v1/delete-font-family/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSaveFontFamilyResult =  mockMvc.perform(requestSaveFontFamilyManagerBuilder);
        var managerDeleteFontFamilyResult =  mockMvc.perform(requestDeleteFontFamilyManagerBuilder);
        //expected
        managerSaveFontFamilyResult.andExpect(status().isUnauthorized());
        managerDeleteFontFamilyResult.andExpect(status().isUnauthorized());
    }

    @Test
    void projectControllerUsingAccessToken_returnsIsOk() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestGetAllProjectsManagerBuilder = get("/project/api/v1/get-all")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetProjectByIdManagerBuilder = get("/project/api/v1/get/1")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerGetAllProjectsResult =  mockMvc.perform(requestGetAllProjectsManagerBuilder);
        var managerGetProjectByIdResult =  mockMvc.perform(requestGetProjectByIdManagerBuilder);
        //expected
        managerGetAllProjectsResult.andExpect(status().isOk());
        managerGetProjectByIdResult.andExpect(status().isOk());
    }

    @Test
    void projectControllerUsingAccessToken_returnsAccessDenied() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        var requestDeleteProjectByIdBuilder = delete("/project/api/v1/delete-project/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestCreateProjectManagerBuilder = post("/project/api/v1/create-project")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestEditProjectManagerBuilder = put("/project/api/v1/edit-project")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerDeleteProjectByIdResult =  mockMvc.perform(requestDeleteProjectByIdBuilder);
        var managerCreateProjectResult =  mockMvc.perform(requestCreateProjectManagerBuilder);
        var managerEditProjectResult =  mockMvc.perform(requestEditProjectManagerBuilder);
        //expected
        managerDeleteProjectByIdResult.andExpect(status().isForbidden());
        managerCreateProjectResult.andExpect(status().isForbidden());
        managerEditProjectResult.andExpect(status().isForbidden());
    }

    @Test
    void projectControllerUsingRefreshToken_returnsIsUnauthorized() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));
        var requestDeleteProjectByIdBuilder = delete("/project/api/v1/delete-project/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestCreateProjectManagerBuilder = post("/project/api/v1/create-project")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestEditProjectManagerBuilder = put("/project/api/v1/edit-project")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerDeleteProjectByIdResult =  mockMvc.perform(requestDeleteProjectByIdBuilder);
        var managerCreateProjectResult =  mockMvc.perform(requestCreateProjectManagerBuilder);
        var managerEditProjectResult =  mockMvc.perform(requestEditProjectManagerBuilder);
        //expected
        managerDeleteProjectByIdResult.andExpect(status().isUnauthorized());
        managerCreateProjectResult.andExpect(status().isUnauthorized());
        managerEditProjectResult.andExpect(status().isUnauthorized());
    }

    @Test
    @Transactional
    void userControllerUsingAccessToken_returnsIsOk() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));
        User checkUser = userRepository.findById(clientId).get();

        String usernameToSave = "user314ITSelfEditUser";
        String email = "EmailAddressIT321SelfEditUser@mail.ru";
        String avatarPath = "AvatarPathSelfEditUserIT1256";

        checkUser.setUsername(usernameToSave);
        checkUser.setEmail(email);
        checkUser.setAvatarPath(avatarPath);

        UserSafeDto userSafeDto = UserSafeDto.mapSelfToUserSafeDto(checkUser);
        String contentBody = mapper.writeValueAsString(userSafeDto);
        var requestSelfEditBuilder = put("/user/api/v1/self-edit")
                .content(contentBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetAllUsersBuilder =  get("/user/api/v1/get-all")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestGetUserByIdBuilder =  get("/user/api/v1/get/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        var managerSelfEditResult =  mockMvc.perform(requestSelfEditBuilder);
        var managerGetAllUsersResult =  mockMvc.perform(requestGetAllUsersBuilder);
        var managerGetUserByIdResult =  mockMvc.perform(requestGetUserByIdBuilder);
        //expected
        managerSelfEditResult.andExpect(status().isOk());
        managerGetAllUsersResult.andExpect(status().isOk());
        managerGetUserByIdResult.andExpect(status().isOk());
    }


    @Test
    void userControllerUsingAccessToken_returnsAccessDenied() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", accessToken));

        var requestEditFullUserBuilder =  put("/user/api/v1/edit-full-user")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestLockUserBuilder =  put("/user/api/v1/set-lock-user/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestUnlockUserBuilder =  put("/user/api/v1/set-unlock-user/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteUserBuilder =  delete("/user/api/v1/delete-user/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());

        //then
        var managerEditFullUserResult =  mockMvc.perform(requestEditFullUserBuilder);
        var managerLockUserResult =  mockMvc.perform(requestLockUserBuilder);
        var managerGeUnlockUserResult =  mockMvc.perform(requestUnlockUserBuilder);
        var managerDeleteUserResult =  mockMvc.perform(requestDeleteUserBuilder);
        //expected
        managerEditFullUserResult.andExpect(status().isForbidden());
        managerLockUserResult.andExpect(status().isForbidden());
        managerGeUnlockUserResult.andExpect(status().isForbidden());
        managerDeleteUserResult.andExpect(status().isForbidden());
    }

    @Test
    void userControllerUsingRefreshToken_returnsIsUnauthorized() throws  Exception
    {
        //given
        Header authHeader = new Header("Authorization",  String.format("Bearer %s", refreshToken));

        var requestEditFullUserBuilder =  put("/user/api/v1/edit-full-user")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestLockUserBuilder =  put("/user/api/v1/set-lock-user/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestUnlockUserBuilder =  put("/user/api/v1/set-unlock-user/5")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        var requestDeleteUserBuilder =  delete("/user/api/v1/delete-user/2")
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());

        //then
        var managerEditFullUserResult =  mockMvc.perform(requestEditFullUserBuilder);
        var managerLockUserResult =  mockMvc.perform(requestLockUserBuilder);
        var managerGeUnlockUserResult =  mockMvc.perform(requestUnlockUserBuilder);
        var managerDeleteUserResult =  mockMvc.perform(requestDeleteUserBuilder);
        //expected
        managerEditFullUserResult.andExpect(status().isUnauthorized());
        managerLockUserResult.andExpect(status().isUnauthorized());
        managerGeUnlockUserResult.andExpect(status().isUnauthorized());
        managerDeleteUserResult.andExpect(status().isUnauthorized());
    }
}
