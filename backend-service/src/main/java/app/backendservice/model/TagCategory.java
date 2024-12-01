package app.backendservice.model;

import java.util.List;


import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Entity
@Table(name = "tag_category")
@AllArgsConstructor
@Builder
public class TagCategory 
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(name="title", unique=true)
    @Size(min=1,max=255, message = "Title must between 1 and 255" )
    private String title;

    @OneToMany(cascade = CascadeType.REMOVE)
    @JoinColumn(name="tag_category_id", insertable = false, updatable = false)
    private List<Tag> tags; 

    public void addTag(Tag tag)
    {
        this.tags.add(tag);
    }
}
