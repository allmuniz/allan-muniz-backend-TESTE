package br.com.sysmap.bootcamp.domain.service;

import br.com.sysmap.bootcamp.domain.entities.AlbumEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.repositories.AlbumRepository;
import br.com.sysmap.bootcamp.domain.service.integration.SpotifyApi;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.http.ParseException;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;


@RequiredArgsConstructor
@Slf4j
@Service
public class AlbumService {

    private final Queue queue;
    private final RabbitTemplate template;

    private final SpotifyApi spotifyApi;
    private final AlbumRepository albumRepository;


    public void teste() {
        log.info("Teste");
        WalletDto walletDto = new WalletDto();
        walletDto.setTeste("Teste Allan Muniz");
        this.template.convertAndSend(queue.getName(), walletDto);
    }

    public List<AlbumModel> getAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {
        return this.spotifyApi.getAlbums(search);
    }

    public List<AlbumModel> SearchAndSaveAlbums(String search) throws IOException, ParseException, SpotifyWebApiException {

        List<AlbumModel> albums = this.getAlbums(search);
        for (AlbumModel album : albums){
            var albumEntity = AlbumEntity.builder()
                    .albumType(String.valueOf(album.getAlbumType()))
                    .artists(Arrays.toString(album.getArtists()))
                    .externalUrl(String.valueOf(album.getExternalUrl()))
                    .albumId(album.getId())
                    .images(Arrays.toString(album.getImages()))
                    .releaseDate(album.getReleaseDate())
                    .value(album.getValue())
                    .build();
            this.albumRepository.save(albumEntity);
        }
        return albums;
    }
}
