package br.com.sysmap.bootcamp.domain;

import br.com.sysmap.bootcamp.BootcampSysmap2024IntegrationApi;
import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repositories.AlbumRepository;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.apache.hc.core5.http.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BootcampSysmap2024IntegrationApi.class)
@AutoConfigureMockMvc(addFilters = false)
public class AlbumServiceTest {


    @InjectMocks
    private SpotifyApi spotifyApi;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RabbitTemplate template;

    @Mock
    private Queue queue;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @Autowired
    private AlbumService albumService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("user@example.com");
    }

    @Test
    @DisplayName("Should return saved albums")
    public void should_return_saved_albums() throws IOException, ParseException, SpotifyWebApiException {
        List<AlbumEntity> mockAlbums = Arrays.asList(
                AlbumEntity.builder()
                        .id(1L)
                        .name("Album1")
                        .idSpotify("1")
                        .artistName("Artist1")
                        .imageUrl("Image1")
                        .value(BigDecimal.TEN)
                        .userId(0L)
                        .build(),
                AlbumEntity.builder()
                        .id(2L)
                        .name("Album2")
                        .idSpotify("2")
                        .artistName("Artist2")
                        .imageUrl("Image2")
                        .value(BigDecimal.valueOf(20.00))
                        .userId(0L)
                        .build()
        );
        when(albumRepository.findByIdSpotify("1")).thenReturn(Optional.empty());
        when(albumRepository.findByIdSpotify("2")).thenReturn(Optional.empty());

        List<AlbumModel> albums = spotifyApi.getAlbums("search");

        verify(albumRepository, times(2)).save(any(AlbumEntity.class));
    }

    @Test
    @DisplayName("Should return a purchased album")
    void should_return_a_purchased_album() {
        UserEntity user = Mockito.mock(UserEntity.class);
        when(user.getId()).thenReturn(1L);
        when(user.getEmail()).thenReturn("user@example.com");
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        AlbumEntity album = new AlbumEntity();
        album.setUserId(0);
        album.setValue(BigDecimal.TEN);
        when(albumRepository.findByIdSpotify(anyString())).thenReturn(Optional.of(album));
        albumService.saleAlbum("spotifyId");

        assertEquals(user.getId(), album.getUserId());

        WalletDto expectedWalletDto = new WalletDto("user@example.com", BigDecimal.TEN);
        verify(template, times(1)).convertAndSend(queue.getName(), expectedWalletDto);
    }

    @Test
    void myCollection() {
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn("test@example.com");

        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@example.com")
                .password("password")
                .build();
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(userEntity));

        List<AlbumEntity> albums = Arrays.asList(
                AlbumEntity.builder().id(1L).name("Album 1").artistName("Artist 1").userId(userEntity.getId()).build(),
                AlbumEntity.builder().id(2L).name("Album 2").artistName("Artist 2").userId(userEntity.getId()).build()
        );
        when(albumRepository.findAllByUserId(userEntity.getId())).thenReturn(albums);

        List<AlbumEntity> result = albumService.myCollection();

        assertEquals(albums.size(), result.size());
        assertEquals(albums, result);
    }

//    @Test
//    @DisplayName("Should delete an album from the collection")
//    public void should_delete_album_from_collection() {
//        UserEntity user = new UserEntity();
//        user.setId(1L);
//        user.setEmail("user@example.com");
//        when(albumService.getUser()).thenReturn(user);
//
//        AlbumEntity albumToDelete = new AlbumEntity();
//        albumToDelete.setId(1L);
//        albumToDelete.setUserId(user.getId());
//        when(albumRepository.findAllByUserId(user.getId())).thenReturn(Optional.of(albumToDelete));
//
//        ResponseEntity<String> response = albumService.deleteAlbum(albumToDelete.getId());
//
//        verify(albumRepository, times(1)).delete(albumToDelete);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("The id has been removed: " + albumToDelete.getId(), response.getBody());
//    }
}
