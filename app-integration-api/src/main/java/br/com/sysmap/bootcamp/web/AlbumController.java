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

    @PostMapping("/test/{albumId}")
    public void buyAlbum(@PathVariable long albumId){
        this.albumService.buyAlbum(albumId);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AlbumModel>> SearchAndSaveAlbums(@RequestParam("search") String search) throws IOException, ParseException, SpotifyWebApiException {
        var result = this.albumService.SearchAndSaveAlbums(search);
        return ResponseEntity.ok().body(result);
    }
}