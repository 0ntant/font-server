package app.backendservice.controllers;


import app.backendservice.dto.TokensDto;
import app.backendservice.dto.UserSafeDto;
import app.backendservice.dto.UserToAuth;
import app.backendservice.dto.UserToRegistration;
import app.backendservice.model.Project;
import app.backendservice.model.Role;
import app.backendservice.model.User;
import app.backendservice.model.UserTokens;
import app.backendservice.repository.ProjectRepository;
import app.backendservice.repository.RoleRepository;
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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import javax.management.relation.RoleResult;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc(printOnlyOnFailure = false)
public class UserControllerIT
{
    @Autowired
    MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    ProjectRepository projectRepository;

    static Header authHeader;

    static int userId = 1;

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
    void getAllUsers_returnAllSafeUsersDto() throws Exception
    {
        //given
        var requestBuilder = get("/user/api/v1/get-all")
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
    void getUserById_returnSafeUsersDto() throws Exception
    {
        //given
        int userToFindId = 2;
        var requestBuilder = get(String.format("/user/api/v1/get/%s", userToFindId))
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
    @Transactional
    @Rollback(value = false)
    void setUnlockUser_ReturnsFullInfo() throws Exception
    {
        //given
        int userToLockId = 4;
        User userToLock = userRepository.findById(userToLockId).get();
        userToLock.setAccountNonLocked(false);
        userRepository.save(userToLock);
        User cloneUserToLock = userToLock.clone();
        var requestBuilder = put(String.format("/user/api/v1/set-unlock-user/%s", userToLockId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );
        userToLock = userRepository.findById(userToLockId).get();
        assertTrue(userToLock.isAccountNonLocked());

        System.out.println(cloneUserToLock.getUsername());
        System.out.println(cloneUserToLock.getEmail());
        System.out.println(cloneUserToLock.getAvatarPath());

        cloneUserToLock.setModifyDate(userToLock.getModifyDate());
        cloneUserToLock.setAccountNonLocked(true);

        assertEquals(cloneUserToLock, userToLock);
    }



    @Test
    @Transactional
    @Rollback(value = false)
    void setLockUser_ReturnsFullInfo() throws Exception
    {
        //given
        int userToLockId = 4;
        User userToLock = userRepository.findById(userToLockId).get();
        userToLock.setAccountNonLocked(true);
        userRepository.save(userToLock);
        User cloneUserToLock = userToLock.clone();
        var requestBuilder = put(String.format("/user/api/v1/set-lock-user/%s", userToLockId))
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());
        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isOk()
                );
        userToLock = userRepository.findById(userToLockId).get();
        assertFalse(userToLock.isAccountNonLocked());
        assertNotNull(userToLock.getUserTokens());
        assertNull(userToLock.getUserTokens().getJwtRefresh());

        System.out.println(cloneUserToLock.getUsername());
        System.out.println(cloneUserToLock.getEmail());
        System.out.println(cloneUserToLock.getAvatarPath());

        cloneUserToLock.setUserTokens(userToLock.getUserTokens());
        cloneUserToLock.setModifyDate(userToLock.getModifyDate());
        cloneUserToLock.setAccountNonLocked(false);

        assertEquals(cloneUserToLock, userToLock);
    }

    @Test
    @Transactional
    @Rollback(value = true)
    void selfEditUser_returnsUserSafeDto () throws Exception
    {
        //given
        User checkUser = userRepository.findById(userId).get();

        String usernameToSave = "user314ITSelfEditUser";
        String email = "EmailAddressIT321SelfEditUser@mail.ru";
        String avatarPath = "AvatarPathSelfEditUserIT1256";

        List<Role> userRoles = checkUser.getRoles();
        List<Project> userProjects = checkUser.getProjects();

        boolean userIsEnabled = checkUser.isEnabled();
        boolean userIsAccountNonExpired = checkUser.isAccountNonExpired();
        boolean userIsAccountNonLocked = checkUser.isAccountNonLocked();
        boolean userIsCredentialsNonExpired = checkUser.isCredentialsNonExpired();

        UserTokens userTokens = checkUser.getUserTokens();

        checkUser.setUsername(usernameToSave);
        checkUser.setEmail(email);
        checkUser.setAvatarPath(avatarPath);

        UserSafeDto userSafeDto = UserSafeDto.mapSelfToUserSafeDto(checkUser);
        String contentBody = mapper.writeValueAsString(userSafeDto);
        var requestBuilder = put("/user/api/v1/self-edit")
                .content(contentBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());;

        //then
        mockMvc.perform(requestBuilder)
                //expected
                .andExpectAll(
                        status().isOk()
                );

        assertTrue(userRepository.findById(userId).isPresent());
        checkUser = userRepository.findById(userId).get();

        assertEquals(usernameToSave, checkUser.getUsername());
        assertEquals(email, checkUser.getEmail());
        assertEquals(avatarPath, checkUser.getAvatarPath());

        assertEquals(userRoles, checkUser.getRoles());
        // assertEquals(userProjects, checkUser.getProjects());
        // expected: org.hibernate.collection.spi.PersistentBag@466fb9be<[]> but was: java.util.ArrayList@123e87cd<[]>

        assertEquals(userIsEnabled, checkUser.isEnabled());
        assertEquals(userIsAccountNonExpired, checkUser.isAccountNonExpired());
        assertEquals(userIsAccountNonLocked, checkUser.isAccountNonLocked());
        assertEquals(userIsCredentialsNonExpired, checkUser.isCredentialsNonExpired());

        assertEquals(userTokens, checkUser.getUserTokens());
    }

//    {
//        "id": 2,
//        "username": "User111",
//        "email": "usus@re.ru",
//        "projects": [],
//        "enabled": true,
//        "accountNonLocked": false,
//        "accountNonExpired": true,
//        "credentialsNonExpired": true
//    }
    @Test
    @Transactional
    @Rollback(value = false)
    void editFullUser_returnsUserSafeDto() throws Exception
    {
        //given
        int userEditedId = 5;
        User checkUser = userRepository.findById(userEditedId).get();

        String usernameToSave = "user121IT";
        String email = "EmailAddressIT12@mail.ru";
        String avatarPath = "AvatarPathIT12";

        List<Role> userRoles = checkUser.getRoles();
        List<Project> userProjects = checkUser.getProjects();

        boolean userIsEnabled = checkUser.isEnabled();
        boolean userIsAccountNonExpired = checkUser.isAccountNonExpired();
        boolean userIsAccountNonLocked = checkUser.isAccountNonLocked();
        boolean userIsCredentialsNonExpired = checkUser.isCredentialsNonExpired();

        UserTokens userTokens = checkUser.getUserTokens();

        checkUser.setUsername(usernameToSave);
        checkUser.setEmail(email);
        checkUser.setAvatarPath(avatarPath);

        UserSafeDto userSafeDto = UserSafeDto.mapToUserSafeDto(checkUser);

        String contentBody = mapper.writeValueAsString(userSafeDto);
        var requestBuilder = put("/user/api/v1/edit-full-user")
                .content(contentBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .header(authHeader.getName(), authHeader.getValues());;

        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isOk()
                );

        assertTrue(userRepository.findById(userEditedId).isPresent());
        checkUser = userRepository.findById(userEditedId).get();

        assertEquals(usernameToSave, checkUser.getUsername());
        assertEquals(email, checkUser.getEmail());
        assertEquals(avatarPath, checkUser.getAvatarPath());

        assertEquals(userRoles, checkUser.getRoles());
       // assertEquals(userProjects, checkUser.getProjects());
       // expected: org.hibernate.collection.spi.PersistentBag@466fb9be<[]> but was: java.util.ArrayList@123e87cd<[]>

        assertEquals(userIsEnabled, checkUser.isEnabled());
        assertEquals(userIsAccountNonExpired, checkUser.isAccountNonExpired());
        assertEquals(userIsAccountNonLocked, checkUser.isAccountNonLocked());
        assertEquals(userIsCredentialsNonExpired, checkUser.isCredentialsNonExpired());

        assertEquals(userTokens, checkUser.getUserTokens());
    }


//    {
//      "username":"user61IT",
//      "password": "1234",
//      "email": "EmailAddress@mail.ru",
//      "avatarPath":"AvatarPathIT"
//    }
    @Test
    @Transactional
    @Rollback(value = false)
    void regUser_returnsUserSafeDto () throws Exception
    {
        //given
        User checkUser ;
        String usernameToSave = "user61IT";

        String password = "P@ssss12341234";
        String email = "EmailAddressIT@mail.ru";
        String avatarPath = "AvatarPathIT";

        UserToRegistration userSafeDto = new UserToRegistration(usernameToSave, password, email, avatarPath);

        String contentBody = mapper.writeValueAsString(userSafeDto);
        var requestBuilder = post("/user/api/v1/reg-user")
                .content(contentBody)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON);
        //then
        mockMvc.perform(requestBuilder)
        //expected
                .andExpectAll(
                        status().isCreated()
                );
        assertTrue(userRepository.findByUsername(usernameToSave).isPresent());

        checkUser = userRepository.findByUsername(usernameToSave).get();

        assertEquals(usernameToSave, checkUser.getUsername());
        assertEquals(1, checkUser.getAuthorities().size());
        assertEquals("ROLE_CLIENT", checkUser.getRoles().get(0).getTitle());
        assertEquals(email, checkUser.getEmail());
        assertEquals(avatarPath, checkUser.getAvatarPath());

        assertNull(checkUser.getProjects());

        assertTrue(checkUser.isAccountNonExpired());
        assertTrue(checkUser.isEnabled()); //fix 1.7.0
        assertTrue(checkUser.isCredentialsNonExpired());
        assertTrue(checkUser.isAccountNonLocked());
    }
}
