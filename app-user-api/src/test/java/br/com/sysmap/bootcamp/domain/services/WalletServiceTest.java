package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
class WalletServiceTest {

    @Autowired
    private WalletService walletService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private WalletRepository walletRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should save wallet for user")
    public void should_save_wallet_for_user() {
        long userId = 1L;
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        walletService.saveWallet(userId);

        verify(walletRepository, times(1)).save(any(WalletEntity.class));
    }

    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found")
    public void should_throw_UserNotFoundException_when_user_is_not_found() {
        long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> walletService.saveWallet(userId));
    }

    @Test
    public void testCredit() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("test@example.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);
        when(userRepository.findByEmail("test@example.com")).thenReturn(optionalUserEntity);

        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(1L);
        walletEntity.setUserId(1L);
        walletEntity.setBalance(BigDecimal.valueOf(50));
        when(walletRepository.findById(1L)).thenReturn(Optional.of(walletEntity));

        walletService.credit(BigDecimal.valueOf(100));

        assertEquals(BigDecimal.valueOf(150), walletEntity.getBalance());
    }

    @Test
    @DisplayName("Should return a success for the debit operation")
    public void should_return_a_success_for_the_debit_operation() {
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
    @DisplayName("Should return an insufficient balance error")
    public void should_return_an_insufficient_balance_error(){
        String testEmail = "test@example.com";
        BigDecimal testValue = new BigDecimal(300);
        BigDecimal initialBalance = new BigDecimal(100);

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

    @Test
    public void testMyWallet() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("test@example.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);
        when(userRepository.findByEmail("test@example.com")).thenReturn(optionalUserEntity);

        WalletEntity walletEntity = new WalletEntity();
        walletEntity.setId(1L);
        walletEntity.setUserId(1L);
        when(walletRepository.findByUserId(1L)).thenReturn(Optional.of(walletEntity));

        WalletEntity returnedWallet = walletService.myWallet();

        assertNotNull(returnedWallet);
        assertEquals(1L, returnedWallet.getUserId());
    }
}


