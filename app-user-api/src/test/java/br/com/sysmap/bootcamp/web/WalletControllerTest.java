package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.entities.wallet.exceptions.WalletNotFoundException;
import br.com.sysmap.bootcamp.domain.services.WalletService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class WalletControllerTest {

    @Autowired
    private WalletController walletController;

    @MockBean
    private WalletService walletService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //<------------------- Test POST("/wallet/credit/{value}") ------------------->

    @Test
    public void testCreditWallet_success() {
        BigDecimal creditValue = new BigDecimal(100);

        WalletEntity walletEntity = WalletEntity.builder()
                .userId(1L)
                .balance(new BigDecimal(200))
                .build();

        when(walletService.credit(creditValue)).thenReturn(walletEntity);

        ResponseEntity<Object> response = walletController.creditWallet(creditValue);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(walletEntity);
    }

    @Test
    public void testCreditWallet_walletNotFound() {
        BigDecimal creditValue = new BigDecimal(100);

        when(walletService.credit(creditValue)).thenThrow(new WalletNotFoundException());

        ResponseEntity<Object> response = walletController.creditWallet(creditValue);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(String.class);
    }

    //<------------------- Test GET("/wallet") ------------------->

    @Test
    public void testSearchWallet_success() {
        WalletEntity walletEntity = WalletEntity.builder()
                .userId(1L)
                .balance(new BigDecimal(200))
                .build();

        when(walletService.myWallet()).thenReturn(walletEntity);

        ResponseEntity<Object> response = walletController.searchWallet();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(walletEntity);
    }

    @Test
    public void testSearchWallet_walletNotFound() {
        when(walletService.myWallet()).thenThrow(new WalletNotFoundException());

        ResponseEntity<Object> response = walletController.searchWallet();

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(String.class);
    }
}
