package app.backendservice.api.v1;

import app.backendservice.dto.UserSafeDto;
import app.backendservice.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping(path="/role/api/v1",
        produces={"application/json"})
public class RoleController
{
    private final RoleService roleService;

    @PutMapping("set-role-manager/{id}")
    public UserSafeDto setRoleManager(@PathVariable int id)
    {
        return roleService.setRoleManagerToUser(id);
    }

    @PutMapping("set-role-admin/{id}")
    public UserSafeDto setRoleAdmin(@PathVariable int id)
    {
        return roleService.setRoleAdminToUser(id);
    }

    @PutMapping("set-role-developer/{id}")
    public UserSafeDto setRoleDeveloper(@PathVariable int id)
    {
        return roleService.setRoleDeveloperToUser(id);
    }

}
