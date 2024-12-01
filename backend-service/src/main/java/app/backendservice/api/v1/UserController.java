package app.backendservice.api.v1;


import app.backendservice.dto.*;
import app.backendservice.service.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping(path = "user/api/v1",
                produces={"application/json"})
public class UserController
{
    final private UserService userService;

    @GetMapping("get-all")
    public List<UserSafeDto> getAll()
    {
        return userService.getAll();
    }

    @GetMapping("get/{id}")
    public UserSafeDto get(@PathVariable int id)
    {
        return userService.getFullSafeInfo(id);
    }

    @DeleteMapping("delete-user/{id}")
    public UserSafeDto delete(@PathVariable int id)
    {
        return userService.deleteUserById(id);
    }

    @PutMapping("edit-full-user")
    public UserSafeDto edit(@Valid @RequestBody UserSafeDto userSafeDto)
    {
        return userService.editFullUser(userSafeDto);
    }

//    @PutMapping("edit-user-role")
//    public UserSafeDto editRole(@Valid @RequestBody UserSafeDto userSafeDto)
//    {
//        return userService.userSetRoles(userSafeDto);
//    }

    @PostMapping("reg-user")
    @ResponseStatus(HttpStatus.CREATED)
    public String regUser(@Valid @RequestBody UserToRegistration userToRegistration)
    {
        return userService.registrationUser(userToRegistration);
    }

    @PutMapping("self-edit")
    public UserSafeDto selfEdit(@Valid @RequestBody UserSafeDto userToSelfEdit)
    {
        return userService.selfEdit(userToSelfEdit);
    }

    @PutMapping("set-lock-user/{id}")
    public UserSafeDto setLockUser(@PathVariable int id)
    {
        return userService.lockUser(id);
    }

    @PutMapping("set-unlock-user/{id}")
    public UserSafeDto setUnlockUser(@PathVariable int id)
    {
        return userService.unlockUser(id);
    }


}
