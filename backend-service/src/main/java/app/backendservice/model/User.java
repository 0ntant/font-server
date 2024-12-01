package app.backendservice.model;


import jakarta.persistence.*;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.UniqueElements;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;

import java.util.Collection;
import java.util.Date;
import java.util.List;


@Data
@Entity
@Table(name= "sh_users", uniqueConstraints = @UniqueConstraint(columnNames = {"username"}))
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Validated
public class User implements UserDetails, Cloneable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "password")
    @NotNull(message = "Password must be not null")
    private String password;

    @Column(name ="username", unique = true)
    @NotNull(message = "Username must be not null")
    @Size(min = 1, max = 255, message = "User's name must be between 1 and 255")
    private String username;

    @Column(name ="email", unique = true)
    @NotNull(message = "Email must be not null")
    @Size(min = 5, max = 255, message = "Email's size must be between 5 and 255")
    @Email(message = "Provide correct email address")
    private String email;

    @Column(name = "avatar_path", unique = true)
    private String avatarPath;

    @Column(name = "is_enabled")
    @NotNull(message = "IsEnabled must be not null")
    private boolean isEnabled;

    @Column(name = "is_account_non_locked")
    @NotNull(message = "isAccountNoNLocked must be not null")
    private boolean isAccountNonLocked;

    @Column(name = "is_account_non_expired")
    @NotNull(message = "isAccountNotExpired must be not null")
    private  boolean isAccountNonExpired;

    @Column(name = "is_credentials_non_expired")
    @NotNull(message = "isCredentialsNotExpired must be not null")
    private  boolean isCredentialsNonExpired;

    @Column(name = "create_date")
    @NotNull(message = "Create date must be not null")
    private Date createDate;

    @Column(name = "modify_date")
    @NotNull(message = "Modify date must be not null")
    private Date modifyDate;

    @ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER)
    @JoinTable(
            name = "sh_user_has_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private List<Role> roles;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn
    private UserTokens userTokens;

    @ManyToMany(targetEntity = Project.class)
    @JoinTable(
            name= "sh_user_has_project",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "project_id")
    )
    private List<Project> projects;

    public void addRole(Role role)
    {
        this.roles.add(role);
    }

    public void addProject(Project project)
    {
        this.projects.add(project);
    }

    public void removeRole(Role role)
    {
        this.roles.remove(role);
    }

    public void removeAllRoles()
    {
        this.roles.clear();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities()
    {
        return this.getRoles();
    }

    @Override
    public String toString ()
    {
        return "";
    }

    @Override
    public User clone()
    {
        try
        {
            User clone = (User) super.clone();

            clone.setProjects(this.getProjects());
            clone.setRoles(this.getRoles());
            clone.setUserTokens(this.getUserTokens());

            return clone;
        }
        catch (CloneNotSupportedException e)
        {
            throw new AssertionError();
        }
    }
}
