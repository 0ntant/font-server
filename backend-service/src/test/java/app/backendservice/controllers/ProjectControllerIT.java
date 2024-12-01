package app.backendservice.controllers;

import app.backendservice.dto.ProjectDto;
import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserProjectDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.model.Project;
import app.backendservice.model.User;
import app.backendservice.repository.ProjectRepository;
import app.backendservice.repository.UserRepository;
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

import java.time.Instant;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class ProjectControllerIT
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    ProjectRepository projectRepository;

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
    void getAllProject_returnsAllProjectDto() throws Exception
    {
        //given
        var requestBuilder = get("/project/api/v1/get-all")
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
    void getProjectById_returnsProjectDto() throws Exception
    {
        //given
        var requestBuilder = get("/project/api/v1/get/1")
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
    void createProject_returnsProjectDto() throws Exception
    {
        //given
        List<User> projectUsers = new LinkedList<>();
        projectUsers.add(userRepository.findById(2).get());
        projectUsers.add(userRepository.findById(3).get());

        Project projectToMap = new Project();
        projectToMap.setTitle("Title1IT");
        projectToMap.setThumbnailPath("SomePath");
        projectToMap.setUsers(projectUsers);

        ProjectDto projectDto = ProjectDto.mapToProjectDtoFullInfo(projectToMap);
        String contentBody = mapper.writeValueAsString(projectDto);

        var requestBuilder = post("/project/api/v1/create-project")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(contentBody)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isCreated()
                );
    }

    @Test
    void editProject_returnsProjectDto() throws Exception
    {
        //given
        List<User> projectUsers = new LinkedList<>();
        projectUsers.add(userRepository.findById(2).get());
        projectUsers.add(userRepository.findById(3).get());

        Project projectToMap = projectRepository.findById(3).get();
        projectToMap.setTitle("Title2IT");
        projectToMap.setThumbnailPath("SomePath");
        projectToMap.setUsers(projectUsers);

        ProjectDto projectDto = ProjectDto.mapToProjectDtoFullInfo(projectToMap);
        String contentBody = mapper.writeValueAsString(projectDto);

        var requestBuilder = put("/project/api/v1/edit-project")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(contentBody)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
    }
}
