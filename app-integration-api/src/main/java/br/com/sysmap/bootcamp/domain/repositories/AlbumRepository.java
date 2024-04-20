package br.com.sysmap.bootcamp.domain.repositories;

import br.com.sysmap.bootcamp.domain.entities.album.AlbumEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AlbumRepository extends JpaRepository<AlbumEntity, Long> {
    List<AlbumEntity> findAllByUserId(long userId);
    Optional<AlbumEntity> findByIdSpotify(String spotifyId);
}
