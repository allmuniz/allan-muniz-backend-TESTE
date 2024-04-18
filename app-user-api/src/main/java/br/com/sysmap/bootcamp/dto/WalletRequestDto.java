package br.com.sysmap.bootcamp.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WalletRequestDto {

    @Schema(example = "Id of your user")
    private long userId;
}
