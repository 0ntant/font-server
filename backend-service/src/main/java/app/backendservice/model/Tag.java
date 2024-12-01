package app.backendservice.model;

import java.util.List;



import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;



@Data
@Entity
@NoArgsConstructor
@Table(name = "tag")
public class Tag 
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id 
    private int id;

    @Column(name="title", unique=true)
    @Size(min=1,max=255, message = "title must between 1 and 255" )
    private String title;

    @Column(name="friendly_name", unique = true)
    @Size(min=1,max=255, message = "Friendly name must between 1 and 255" )
    private String friendlyName;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @NotNull(message = "Tag category must be not null")
    //@JoinColumn(name="tag_category_id", insertable = false, updatable = false)
    private TagCategory tagCategory; 

    @ManyToMany(targetEntity = FontFamily.class)
    @JoinTable(
        name = "font_family_has_tag", 
        joinColumns = @JoinColumn(name = "tag_id"), 
        inverseJoinColumns = @JoinColumn(name = "font_family_id")) 
    private List<FontFamily> fontFamilies;

    public void addFontFamily(FontFamily fontFamily) 
    {
        this.fontFamilies.add(fontFamily);
    }

    public Tag setFriendlyName(String friendlyName)
    {
        this.friendlyName = friendlyName;
        return this;
    }
}
