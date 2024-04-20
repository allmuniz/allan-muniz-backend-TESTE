package br.com.sysmap.bootcamp.dto;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthDto {

    private String email;
    private String password;
    private Long id;
    private String token;
}
