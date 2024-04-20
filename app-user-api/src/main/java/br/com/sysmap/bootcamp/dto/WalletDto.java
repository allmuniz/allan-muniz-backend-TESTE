package br.com.sysmap.bootcamp.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class WalletDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String email;
    private BigDecimal value;
}