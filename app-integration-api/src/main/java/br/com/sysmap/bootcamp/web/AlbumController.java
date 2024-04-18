package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.apache.hc.core5.http.ParseException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;

import java.io.IOException;
import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/teste")
    public void teste() {
        this.albumService.teste();
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumModel>> SearchAndSaveAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        var result = this.albumService.SearchAndSaveAlbums(search);
        return ResponseEntity.ok().body(result);
    }

//    @GetMapping("/all")
//    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
//        return ResponseEntity.ok(this.albumService.getAlbums(search));
//    }

}