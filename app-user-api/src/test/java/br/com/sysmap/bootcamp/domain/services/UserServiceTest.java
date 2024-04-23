package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@RunWith(MockitoJUnitRunner.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private WalletService walletService;

    @InjectMocks
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private SecurityContext securityContext;

    @BeforeEach
    public void setup() {
        SecurityContextHolder.setContext(securityContext);
    }

    @Test
    @DisplayName("Should save the user and create the wallet")
    public void should_save_the_user_and_create_the_wallet(){
        UserDto userDto = UserDto.builder().name("Test User").email("test@email.com").password("password").build();
        when(userRepository.findByEmail(userDto.getEmail())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("encodedPassword");

        userService.save(userDto);

        verify(userRepository, times(1)).save(Mockito.any(UserEntity.class));
        verify(walletService, times(1)).saveWallet(Mockito.anyLong());
    }

    @Test
    @DisplayName("Should authenticate the user")
    public void should_authenticate_the_user() {
        AuthDto authDto = AuthDto.builder().email("test@email.com").password("password123").build();

        UserEntity userEntity = UserEntity.builder().id(1L).email(authDto.getEmail()).password(passwordEncoder.encode(authDto.getPassword())).build();
        when(userRepository.findByEmail("test@email.com")).thenReturn(Optional.ofNullable(userEntity));

        assert userEntity != null;
        when(passwordEncoder.matches(authDto.getPassword(), userEntity.getPassword())).thenReturn(true);

        AuthDto result = userService.auth(authDto);

        String expectedToken = Base64.getEncoder().withoutPadding().encodeToString((userEntity.getEmail() + ":" + userEntity.getPassword()).getBytes());
        assertEquals(authDto.getEmail(), result.getEmail());
        assertEquals(expectedToken, result.getToken());
        assertEquals(userEntity.getId(), result.getId());
    }

    @Test
    @DisplayName("Should return an updated user")
    public void should_return_an_updated_user() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("test@example.com");

        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setEmail("test@example.com");
        Optional<UserEntity> optionalUserEntity = Optional.of(userEntity);
        when(userRepository.findByEmail("test@example.com")).thenReturn(optionalUserEntity);

        UserDto userDto = new UserDto();
        userDto.setName("Test");
        userDto.setEmail("test@example.com");
        userDto.setPassword("test");

        when(passwordEncoder.encode(userDto.getPassword())).thenReturn("hashedPassword");

        UserEntity updatedUser = userService.update(userDto);

        assertNotNull(updatedUser);
        assertEquals("Novo Nome", updatedUser.getName());
        assertEquals("test@example.com", updatedUser.getEmail());
        assertEquals("hashedPassword", updatedUser.getPassword());
    }

    @Test
    @DisplayName("Should find user by id")
    public void should_find_user_by_id() {
        Long userId = 1L;
        UserEntity userEntity = UserEntity.builder().id(userId).build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(userEntity));

        UserEntity result = userService.findById(userId);

        assertEquals(userEntity, result);
    }
    @Test
    @DisplayName("Should throw UserNotFoundException when user is not found by id")
    public void should_throw_UserNotFoundException_when_user_is_not_found_by_id() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.findById(userId));
    }

    @Test
    @DisplayName("Should find all users")
    public void should_find_all_users() {
        List<UserEntity> users = Arrays.asList(
                UserEntity.builder().id(1L).build(),
                UserEntity.builder().id(2L).build()
        );
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.findAll();

        assertEquals(users.size(), result.size());
        assertEquals(users, result);
    }
}
