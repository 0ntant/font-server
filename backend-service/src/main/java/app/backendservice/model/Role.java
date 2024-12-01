package app.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Data
@Entity
@Table(name = "sh_roles")
@NoArgsConstructor
@AllArgsConstructor
public class Role implements GrantedAuthority
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "title", unique = true)
    @NotNull(message = "Title must be not null")
    private  String title;

    @ManyToMany(targetEntity = User.class , fetch = FetchType.EAGER)
    @JoinTable (
            name = "sh_user_has_role",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name ="user_id")
    )
    private List<User> users;

    public void addUser(User user) {this.users.add(user);}

    public void removeUser(User user) {this.users.remove(user);}

    @Override
    public String getAuthority()
    {
        return this.getTitle();
    }


}
