package br.com.sysmap.bootcamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {

    @Schema(example = "Your name")
    private String name;
    @Schema(example = "youremail@email.com")
    private String email;
    @Schema(example = "Your Password")
    private String password;
}
