package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws Exception {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @PostMapping("/sale")
    public void buyAlbum(@RequestParam("spotifyId") String spotifyId){
        this.albumService.saleAlbum(spotifyId);
    }

    @GetMapping("/my-collection")
    public List<AlbumEntity> myCollection(){
        return this.albumService.myCollection();
    }

    @DeleteMapping("/remove/{id}")
    public void removeAlbum(@PathVariable long id){
        albumService.deleteAlbum(id);
    }
}