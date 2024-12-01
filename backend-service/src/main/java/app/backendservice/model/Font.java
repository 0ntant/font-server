package app.backendservice.model;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;


@NoArgsConstructor
@Data
@Entity
@Table(name = "font")
@AllArgsConstructor
@Builder
public class Font
{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id   
    private int id;

    @Column(name="type")
    @NotNull(message = "Type must be not null")
    @Size(min=1, max = 255, message = "Type must be between 1 and 255")
    private String type;

    @Column(unique = true, name = "file_path")
    @NotNull(message = "FilePath must be not null")
    @Size(min=1, max = 255, message = "FilePath must be between 2 and 255")
    private String filePath;    

    @Column(name = "is_corrupted")
    @NotNull(message = "isCorrupted must be not null")
    boolean isCorrupted; 

    @ManyToOne(fetch = FetchType.LAZY)
   //@JoinColumn(name="font_family_id", insertable = false)
    private FontFamily fontFamily;
}