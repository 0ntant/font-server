package app.backendservice.model;

import java.util.List;

import jakarta.validation.constraints.Size;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.Table;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ManyToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@Data
@Entity
@Table(name = "font_family")
@AllArgsConstructor
@Builder
public class FontFamily 
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    private int id;

    @Column(unique = true, name ="title")
    @Size(min = 2, max = 255, message = "Title must be between 2 and 255")
    private String title;

    @ManyToMany(targetEntity = Tag.class)
    @JoinTable(
        name = "font_family_has_tag", 
        joinColumns = @JoinColumn(name = "font_family_id"), 
        inverseJoinColumns = @JoinColumn(name = "tag_id"))
    private List<Tag> tags;

    public void addTag(Tag tag)
    {
        this.tags.add(tag);
    } 

    public void addFont(Font font)
    {
        this.fonts.add(font);
    } 

    @OneToMany
    @JoinColumn(name = "font_family_id")
    private List<Font> fonts;
}
