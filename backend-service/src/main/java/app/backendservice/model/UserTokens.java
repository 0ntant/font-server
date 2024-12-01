package app.backendservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name="sh_user_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserTokens
{
    @Id
    @Column(name = "user_id")
    private int id;

    @Column(name = "jwt_refresh")
    private String jwtRefresh;

    @OneToOne(fetch = FetchType.EAGER)
    @MapsId
    @JoinColumn(name = "user_id")
    private User user;
}
