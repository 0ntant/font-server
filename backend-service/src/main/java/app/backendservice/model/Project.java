package app.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "project")
@NoArgsConstructor
@Validated
public class Project
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name = "title")
    @Size(min = 1, max = 255, message = "Title's size must be between 1 and 255")
    private  String title;

    @Column(name = "thumbnail_path")
    @Size(min = 1, max = 255, message = "Thumbnail path's size must be between 1 and 255")
    private String thumbnailPath;

    @NotNull(message = "Create date must be not null")
    @Column(name = "create_date")
    private Date createDate;

    @NotNull(message = "Modify date must be not null")
    @Column(name = "modify_date")
    private Date modifyDate;

    @ManyToMany(targetEntity = User.class)
    @JoinTable(
            name = "sh_user_has_project",
            joinColumns = @JoinColumn(name = "project_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> users;
}
