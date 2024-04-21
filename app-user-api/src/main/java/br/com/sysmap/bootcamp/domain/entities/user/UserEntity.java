package br.com.sysmap.bootcamp.domain.entities.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_USERS")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Schema(example = "Your name")
    private String name;

    @Schema(example = "youremail@email.com", requiredMode = Schema.RequiredMode.REQUIRED)
    private String email;

    @Schema(example = "Your password", minLength = 6, maxLength = 15, requiredMode = Schema.RequiredMode.REQUIRED)
    private String password;
}