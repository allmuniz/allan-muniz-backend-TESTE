package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import br.com.sysmap.bootcamp.domain.model.AlbumModel;
import br.com.sysmap.bootcamp.domain.service.AlbumService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/albums")
@Tag(name = "Integration", description = "Integration API Spotify")
public class AlbumController {

    private final AlbumService albumService;

    @GetMapping("/all")
    @Operation(summary = "Get all albums from Spotify service by Text parameter",
            description = "This function is responsible for searching for albums on spotify.")
    public ResponseEntity<List<AlbumModel>> getAlbums(@RequestParam("search") String search) throws Exception {
        return ResponseEntity.ok(this.albumService.getAlbums(search));
    }

    @PostMapping("/sale")
    @Operation(summary = "Buy an album",
            description = "This function is responsible for purchasing an album.")
    public void buyAlbum(@RequestParam("spotifyId") String spotifyId){
        this.albumService.saleAlbum(spotifyId);
    }

    @GetMapping("/my-collection")
    @Operation(summary = "Get all albums from my collection",
            description = "This function is responsible for searching my albums.")
    public List<AlbumEntity> myCollection(){
        return this.albumService.myCollection();
    }

    @DeleteMapping("/remove/{id}")
    @Operation(summary = "Remove an album by ID",
            description = "This function is responsible for removing an album.")
    public void removeAlbum(@PathVariable long id){
        albumService.deleteAlbum(id);
    }
}