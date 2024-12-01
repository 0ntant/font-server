package app.backendservice.service;

import app.backendservice.dto.ProjectUserDto;
import app.backendservice.dto.UserSafeDto;
import app.backendservice.dto.UserToRegistration;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.exception.ResourceNotValidatedException;
import app.backendservice.model.*;

import app.backendservice.repository.ProjectRepository;
import app.backendservice.repository.RoleRepository;
import app.backendservice.repository.UserRepository;
import app.backendservice.repository.UserTokensRepository;

import app.backendservice.utils.HashUtil;
import jakarta.validation.ConstraintViolationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.Instant;
import java.util.*;


@Slf4j
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService
{
    final private UserRepository userRepository;

    final private RoleRepository roleRepository;

    final private ProjectRepository projectRepository;

    final private UserTokensRepository userTokensRepository;

    final private HashUtil hashUtil;

    public List<UserSafeDto> getAll()
    {
        return userRepository.findAll()
                .stream()
                .map(UserSafeDto::mapToUserSafeDto)
                .toList();
    }

    private Role findRoleByTitle(String roleTitle)
    {
        return roleRepository.findByTitle(roleTitle)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role with title=%s not found", roleTitle)));
    }

    private User findByUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with username=%s not found", username)));
    }


    private User findById(int id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("User with id=%s not found", id)));
    }

    private void uniqueFieldCheck(int id, String username, String email, String avatarPath) {

        Optional<User> foundByUsername = userRepository.findByUsername(username);
        if (foundByUsername.isPresent() && foundByUsername.get().getId() != id) {
            throw new ResourceNotValidatedException(String.format("User with username=%s already exists", username));
        }
        Optional<User> foundByEmail = userRepository.findByEmail(email);
        if (foundByEmail.isPresent() && foundByEmail.get().getId() != id) {
            throw new ResourceNotValidatedException(String.format("User with email=%s already exists", email));
        }

        if (avatarPath == null)
        {
            return;
        }
        Optional<User> foundByAvatarPath = userRepository.findByAvatarPath(avatarPath);
        if(foundByAvatarPath.isPresent() && foundByAvatarPath.get().getId() != id)
        {
            throw new ResourceNotValidatedException(String.format("User with avatarPath=%s already exists", avatarPath));
        }
    }

    private UserSafeDto userDtoFullInfo(User user){ return UserSafeDto.mapToUserSafeDto(user);}

    private List<Role> findRolesByIds(List<Integer> ids)
    {
        List<Role> foundRoles = roleRepository.findByIdIn(ids);
        if (foundRoles.isEmpty() && !ids.isEmpty())
        {
            throw new ResourceNotFoundException(String.format("Roles with ids: %s not found", ids));
        }
        if (foundRoles.size() != ids.size())
        {
            List<Integer> foundIds = foundRoles
                    .stream()
                    .map(Role::getId)
                    .toList();

            throw new ResourceNotFoundException(String.format("Roles with ids: %s not found", ids.removeAll(foundIds)));
        }

        return foundRoles;
    }

    private List<Project> findProjectsByIds(List<Integer> ids)
    {
        List<Project> foundProjects = projectRepository.findByIdIn(ids);
        if (foundProjects.isEmpty() && !ids.isEmpty())
        {
            throw new ResourceNotFoundException(String.format("Projects with ids: %s not found", ids));
        }
        if (foundProjects.size() != ids.size())
        {
            List<Integer> foundIds = foundProjects
                    .stream()
                    .map(Project::getId)
                    .toList();

            throw new ResourceNotFoundException(String.format("Projects with ids: %s not found", ids.removeAll(foundIds)));
        }
        return foundProjects;
    }

    public UserSafeDto getFullSafeInfo(int id)
    {
        return this.userDtoFullInfo(this.findById(id));
    }


    @Transactional
    public UserSafeDto deleteUserById(int id)
    {
        User userToDelete = this.findById(id);
        userRepository.deleteById(id);

        return this.userDtoFullInfo(userToDelete);
    }


    @Transactional
    public UserSafeDto editFullUser(UserSafeDto userSafeDto)
    {
        User userToSave = this.findById(userSafeDto.getId());
        uniqueFieldCheck(userSafeDto.getId(), userSafeDto.getUsername(), userSafeDto.getEmail(), userSafeDto.getAvatarPath());
        List<Integer> projectsIds = userSafeDto.getProjects()
                .stream()
                .map(ProjectUserDto::getId)
                .toList();

        List<Project> userProjects = this.findProjectsByIds(projectsIds);

        userToSave.setEmail(userSafeDto.getEmail());
        userToSave.setUsername(userSafeDto.getUsername());
        userToSave.setAvatarPath(userSafeDto.getAvatarPath());
        userToSave.setProjects(userProjects);

        userToSave.setEnabled(userSafeDto.isEnabled());
        userToSave.setAccountNonLocked(userSafeDto.isAccountNonLocked());
        userToSave.setAccountNonExpired(userSafeDto.isAccountNonExpired());
        userToSave.setCredentialsNonExpired(userSafeDto.isCredentialsNonExpired());

        userToSave.setModifyDate(Date.from(Instant.now()));

        try
        {
            userRepository.save(userToSave);
        }
        catch (Exception ex)
        {
            throw new ResourceNotValidatedException(ex.getMessage());
        }

        return this.userDtoFullInfo(userToSave);
    }

    @Transactional
    public UserSafeDto selfEdit(UserSafeDto userSafeDto)
    {
        User user = userRepository.findById(Integer.valueOf(SecurityContextHolder.getContext().getAuthentication().getName()))
                .orElseThrow(() -> new UsernameNotFoundException("You must be login in"));
        userSafeDto.setId(user.getId());
        UserSafeDto editedUser = this.editUser(userSafeDto);
        user.getUserTokens().setJwtRefresh(null);

        user.setModifyDate(Date.from(Instant.now()));
        userRepository.save(user);

        return editedUser;
    }


    @Transactional
    public UserSafeDto editUser(UserSafeDto userSafeDto)
    {
        User userToSave = this.findById(userSafeDto.getId());
        uniqueFieldCheck(userSafeDto.getId(), userSafeDto.getUsername(), userSafeDto.getEmail(), userSafeDto.getAvatarPath());
        List<Integer> projectsIds = userSafeDto.getProjects()
                .stream()
                .map(ProjectUserDto::getId)
                .toList();
        List<Project> userProjects = this.findProjectsByIds(projectsIds);

        userToSave.setEmail(userSafeDto.getEmail());
        userToSave.setUsername(userSafeDto.getUsername());
        userToSave.setAvatarPath(userSafeDto.getAvatarPath());
        userToSave.setProjects(userProjects);

        userToSave.setModifyDate(Date.from(Instant.now()));
        try
        {
            userRepository.save(userToSave);
        }
        catch (Exception ex)
        {
            throw new ResourceNotValidatedException(ex.getMessage());
        }

        return this.userDtoFullInfo(userToSave);
    }


    @Transactional
    public UserSafeDto lockUser(int id)
    {
        User userToSave = this.findById(id);
        UserTokens userTokens = userTokensRepository.findById(id)
                        .orElse(new UserTokens());
        userTokens.setUser(userToSave);
        userTokens.setJwtRefresh(null);

        userToSave.setAccountNonLocked(false);
        userToSave.setUserTokens(userTokens);
        userToSave.setModifyDate(Date.from(Instant.now()));

        userRepository.save(userToSave);

        return this.userDtoFullInfo(userToSave);
    }

    @Transactional
    public UserSafeDto unlockUser(int id)
    {
        User userToSave = this.findById(id);

        userToSave.setAccountNonLocked(true);
        userToSave.setModifyDate(Date.from(Instant.now()));

        userRepository.save(userToSave);

        return this.userDtoFullInfo(userToSave);
    }

    @Transactional
    public String registrationUser(UserToRegistration userToRegistration) throws ConstraintViolationException
    {
        User userToSave = new User();
        Role clientRole = this.findRoleByTitle("ROLE_CLIENT");

        this.uniqueFieldCheck(userToSave.getId(), userToRegistration.getUsername(), userToRegistration.getEmail(), userToRegistration.getAvatarPath());

        userToSave.setUsername(userToRegistration.getUsername());
        userToSave.setPassword(hashUtil.bCryptHash(userToRegistration.getPassword()));
        userToSave.setEmail(userToRegistration.getEmail());
        userToSave.setAvatarPath(userToRegistration.getAvatarPath());

        userToSave.setRoles(Arrays.asList(clientRole));

        userToSave.setEnabled(true); // fix in 1.7.0
        userToSave.setAccountNonLocked(true);
        userToSave.setAccountNonExpired(true);
        userToSave.setCredentialsNonExpired(true);

        userToSave.setCreateDate(Date.from(Instant.now()));
        userToSave.setModifyDate(Date.from(Instant.now()));

        userRepository.save(userToSave);

        return String.format("successfully registered user: %s", userToSave.getUsername());
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username=%s not found", username)));
    }


}
