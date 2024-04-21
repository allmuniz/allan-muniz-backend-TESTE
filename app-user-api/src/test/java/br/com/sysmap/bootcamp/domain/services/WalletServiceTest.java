package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private WalletRepository walletRepository;

    @Test
    public void testDebit() {
        UserEntity user = new UserEntity();
        user.setId(1L);

        WalletEntity wallet = new WalletEntity();
        wallet.setBalance(new BigDecimal(100));
        wallet.setPoints(0);
        wallet.setLastUpdate(LocalDateTime.now());

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(user));
        when(walletRepository.findByUserId(anyLong())).thenReturn(Optional.of(wallet));

        WalletDto walletDto = new WalletDto();
        walletDto.setEmail("user@example.com");
        walletDto.setValue(new BigDecimal(50));
        walletService.debit(walletDto);

        DayOfWeek dayOfWeek = LocalDate.now().getDayOfWeek();
        int expectedPoints = switch (dayOfWeek) {
            case MONDAY -> 7;
            case TUESDAY -> 6;
            case WEDNESDAY -> 2;
            case THURSDAY -> 10;
            case FRIDAY -> 15;
            case SATURDAY -> 20;
            case SUNDAY -> 25;
        };

        assertThat(wallet.getBalance()).isEqualTo(new BigDecimal(50));
        assertThat(wallet.getPoints()).isEqualTo(expectedPoints);

        verify(walletRepository, times(1)).save(wallet);
    }
    
    @Test
    public void testDebit_insufficientFunds(){
        // Given (arrange)
        String testEmail = "test@example.com";
        BigDecimal testValue = new BigDecimal(300);
        BigDecimal initialBalance = new BigDecimal(100);

        // Mocks
        UserEntity userMock = new UserEntity();
        userMock.setEmail(testEmail);
        when(userRepository.findByEmail(testEmail)).thenReturn(Optional.of(userMock));

        WalletEntity walletMock = new WalletEntity();
        walletMock.setId(1L);
        walletMock.setUserId(userMock.getId());
        walletMock.setBalance(initialBalance);
        walletMock.setPoints(0);
        when(walletRepository.findByUserId(userMock.getId())).thenReturn(Optional.of(walletMock));

        WalletDto walletDto = new WalletDto(testEmail, testValue);
        walletService.debit(walletDto);
    }
}


