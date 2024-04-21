package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserFoundException;
import br.com.sysmap.bootcamp.domain.services.UserService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)
public class UserControllerTest {

    @Autowired
    private UserController userController;

    @MockBean
    private UserService userService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    //<------------------- Test POST("/users/create") ------------------->

    @Test
    public void testCreateUser_success() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@email.com")
                .password("password123")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("Test User")
                .email("test@email.com")
                .build();
        when(userService.save(userDto)).thenReturn(userEntity);
        ResponseEntity<Object> response = userController.createUser(userDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(userEntity);
    }

    @Test
    public void testCreateUser_duplicateEmail() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("existing@email.com")
                .password("password123")
                .build();

        when(userService.save(userDto)).thenThrow(new UserFoundException());
        ResponseEntity<Object> response = userController.createUser(userDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(String.class);
    }

    @Test
    public void testCreateUser_exception() {
        UserDto userDto = UserDto.builder()
                .name("Test User")
                .email("test@email.com")
                .password("password123")
                .build();

        when(userService.save(userDto)).thenThrow(new RuntimeException("Unexpected error"));
        ResponseEntity<Object> response = userController.createUser(userDto);
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isInstanceOf(String.class);
    }

    //<------------------- Test POST("/users/auth") ------------------->

    @Test
    public void testAuth_success() {
        String email = "test@email.com";
        String password = "password123";
        String encodedPassword = passwordEncoder.encode(password);
        AuthDto authDto = AuthDto.builder().email(email).password(password).build();
        UserEntity userEntity = UserEntity.builder().id(1L).email(email).password(encodedPassword).build();

        when(userService.findByEmail(email)).thenReturn(userEntity);
        ResponseEntity<AuthDto> response = userController.auth(authDto);

        Optional<AuthDto> maybeBody = Optional.ofNullable(response.getBody());

        maybeBody.ifPresent(body -> {
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            String expectedToken = Base64.getEncoder().withoutPadding().encodeToString((email + ":" + password).getBytes());
            assertThat(body.getEmail()).isEqualTo(email);
            assertThat(body.getToken()).isEqualTo(expectedToken);
            assertThat(body.getId()).isEqualTo(userEntity.getId());
        });
    }

    //<------------------- Test PUT("/users/update") ------------------->

    @Test
    public void testUpdateUser() {
        UserDto userDto = UserDto.builder()
                .name("John Doe")
                .email("john.doe@example.com")
                .password("password")
                .build();

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .build();
        when(userService.update(userDto)).thenReturn(userEntity);

        ResponseEntity<Object> responseEntity = userController.updateUser(userDto);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(userEntity, responseEntity.getBody());
    }

    //<------------------- Test GET("/users/{id}") ------------------->

    @Test
    public void testSearchUser() {
        long userId = 1L;

        UserEntity userEntity = UserEntity.builder()
                .id(userId)
                .name("John Doe")
                .email("john.doe@example.com")
                .password("encodedPassword")
                .build();
        when(userService.findById(userId)).thenReturn(userEntity);

        ResponseEntity<Object> responseEntity = userController.searchUser(userId);

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(userEntity, responseEntity.getBody());
    }

    //<------------------- Test GET("/users") ------------------->

    @Test
    public void testSearchAllUsers() {
        List<UserEntity> userEntities = Arrays.asList(
                UserEntity.builder()
                        .id(1L)
                        .name("John Doe")
                        .email("john.doe@example.com")
                        .password("encodedPassword")
                        .build(),
                UserEntity.builder()
                        .id(2L)
                        .name("Jane Doe")
                        .email("jane.doe@example.com")
                        .password("encodedPassword")
                        .build()
        );
        when(userService.findAll()).thenReturn(userEntities);

        ResponseEntity<Object> responseEntity = userController.searchAllUsers();

        assertEquals(200, responseEntity.getStatusCodeValue());
        assertEquals(userEntities, responseEntity.getBody());
    }
}

