package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.BootcampSysmap2024IntegrationApi;
import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repositories.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = BootcampSysmap2024IntegrationApi.class)
@AutoConfigureMockMvc(addFilters = false)
public class AlbumControllerTest {
    @Autowired
    private AlbumController albumController;

    @MockBean
    private AlbumService albumService;

    @MockBean
    private AlbumRepository albumRepository;

    //<-----------------------------GET("/albums/all")<-----------------------------//
    @Test
    @DisplayName("Should return list of albums and save to database")
    public void should_return_list_of_albums_and_save_to_database() throws Exception {
        String searchTerm = "Rock";
        List<AlbumModel> expectedAlbums = createSampleAlbums();

        when(albumService.getAlbums(searchTerm)).thenReturn(expectedAlbums);

        ResponseEntity<List<AlbumModel>> response = albumController.getAlbums(searchTerm);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo(expectedAlbums);

        List<AlbumEntity> savedAlbums = albumRepository.findAll();
        assertThat(savedAlbums);
        for (int i = 0; i < savedAlbums.size(); i++) {
            AlbumModel expectedModel = expectedAlbums.get(i);
            AlbumEntity savedAlbum = savedAlbums.get(i);

            assertThat(savedAlbum.getId()).isNotNull();
            assertThat(savedAlbum.getArtistName()).isEqualTo(expectedModel.getArtists());
            assertThat(savedAlbum.getIdSpotify()).isEqualTo(expectedModel.getId());
            assertThat(savedAlbum.getImageUrl()).isEqualTo(expectedModel.getImages());
            assertThat(savedAlbum.getName()).isEqualTo(expectedModel.getName());
            assertThat(savedAlbum.getValue()).isEqualTo(expectedModel.getValue());
        }
    }
    private List<AlbumModel> createSampleAlbums() {
        return new ArrayList<>();
    }

    //<-----------------------------GET("/albums//my-collection")<-----------------------------//

    @Test
    @DisplayName("Should return a collection of albums for a given user")
    public void should_return_a_collection_of_albums_for_a_given_user(){
        List<AlbumEntity> albumEntity = Arrays.asList(
                AlbumEntity.builder()
                        .id(1L)
                        .name("Album 1")
                        .artistName("Artist 1")
                        .userId(1L)
                        .build(),
                AlbumEntity.builder()
                        .id(2L)
                        .name("Album 2")
                        .artistName("Artist 2")
                        .userId(1L)
                        .build()
        );
        UserEntity userEntity = UserEntity.builder()
                .id(1L)
                .email("test@email.com")
                .password("password123")
                .build();
        when(albumRepository.findAllByUserId(userEntity.getId())).thenReturn(albumEntity);

        assertEquals(2, albumEntity.size());
    }
}

