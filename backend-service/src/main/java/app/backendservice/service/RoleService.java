package app.backendservice.service;

import app.backendservice.dto.UserSafeDto;
import app.backendservice.exception.ResourceNotFoundException;
import app.backendservice.model.Role;
import app.backendservice.model.User;
import app.backendservice.repository.RoleRepository;
import app.backendservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class RoleService
{
    private final RoleRepository roleRepository;

    private final UserRepository userRepository;

    private Role findRoleByTitle(String roleTitle)
    {
        return roleRepository.findByTitle(roleTitle)
                .orElseThrow(() -> new ResourceNotFoundException(String.format("Role with title=%s not found", roleTitle)));
    }

    private User findUserById(int id)
    {
        return userRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id=%s not found", id)));

    }

    private User findByUserUsername(String username)
    {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with username=%s not found", username)));
    }

    private UserSafeDto usersWithRoles(User user){ return UserSafeDto.mapToUserRoles(user);}


    @Transactional
    public  UserSafeDto setRoleClientToUser(int id)
    {
        Role role = this.findRoleByTitle("ROLE_CLIENT");
        User user = this.findUserById(id);
        List<Role> roles = new LinkedList<Role>();
        roles.add(role);

        user.setRoles(roles);
        user.setModifyDate(Date.from(Instant.now()));
        try {
            userRepository.save(user);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
        return this.usersWithRoles(user);
    }

    @Transactional
    public UserSafeDto setRoleManagerToUser(int id)
    {
        Role role = this.findRoleByTitle("ROLE_MANAGER");
        User user = this.findUserById(id);
        List<Role> roles = new LinkedList<Role>();
        roles.add(role);
        user.setRoles(roles);
        user.setModifyDate(Date.from(Instant.now()));
        try {
            roleRepository.save(role);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
        return this.usersWithRoles(user);
    }

    @Transactional
    public  UserSafeDto setRoleAdminToUser(int id)
    {
        Role role = this.findRoleByTitle("ROLE_ADMIN");
        User user = this.findUserById(id);
        List<Role> roles = new LinkedList<Role>();
        roles.add(role);
        user.setRoles(roles);
        user.setModifyDate(Date.from(Instant.now()));
        try {
            userRepository.save(user);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
        return this.usersWithRoles(user);
    }

    @Transactional
    public  UserSafeDto setRoleDeveloperToUser(int id)
    {
        Role role = this.findRoleByTitle("ROLE_DEVELOPER");
        User user = this.findUserById(id);
        List<Role> roles = new LinkedList<Role>();
        roles.add(role);
        user.setRoles(roles);
        user.setModifyDate(Date.from(Instant.now()));
        try {
            userRepository.save(user);
        }
        catch (Exception ex)
        {
            log.error(ex.getMessage());
        }
        return this.usersWithRoles(user);
    }
}
