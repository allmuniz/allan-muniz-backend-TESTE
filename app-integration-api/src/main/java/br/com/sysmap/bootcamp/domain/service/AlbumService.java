package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import br.com.sysmap.bootcamp.domain.entities.album.exceptions.AlbumFoundException;
import br.com.sysmap.bootcamp.domain.entities.album.exceptions.AlbumNotFoundException;
import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repositories.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;


@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;
    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;
    private final UserAlbumService userAlbumService;

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {

        List<AlbumModel> albums = this.spotifyApi.getAlbums(search);
        for (AlbumModel album : albums){
            if (albumRepository.findByIdSpotify(album.getId()).isPresent()){
                throw new AlbumFoundException();
            }
            var albumEntity = AlbumEntity.builder()
                    .name(album.getName())
                    .idSpotify(album.getId())
                    .artistName(Arrays.toString(album.getArtists()))
                    .imageUrl(Arrays.toString(album.getImages()))
                    .value(album.getValue())
                    .userId(0)
                    .build();
            this.albumRepository.save(albumEntity);
        }
        return albums;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void saleAlbum(String spotifyId){
        var user = getUser();

        WalletDto walletDto = new WalletDto();
        var album = albumRepository.findByIdSpotify(spotifyId).orElseThrow(AlbumNotFoundException::new);

        if(album.getUserId() == 0) {
            walletDto.setValue(album.getValue());
            walletDto.setEmail(user.getEmail());
        }

        album.setUserId(user.getId());

        this.template.convertAndSend(queue.getName(), walletDto);
        log.info(walletDto.getEmail());
    }

    public List<AlbumEntity> myCollection(){
        var user = getUser();
        return albumRepository.findAllByUserId(user.getId());
    }

    public ResponseEntity<String> deleteAlbum(long id){
        var user = getUser();
        var collection = albumRepository.findAllByUserId(user.getId());

        Optional<AlbumEntity> albumToDelete = collection.stream()
                .filter(album -> album.getId().equals(id)).findFirst();
        albumToDelete.ifPresent(albumRepository::delete);

        return ResponseEntity.ok().body("The id has been removed: " + id);
    }

    private UserEntity getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return userAlbumService.findByEmail(username);
    }
}
