package br.com.sysmap.bootcamp.domain.entities.album;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_ALBUMS")
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", updatable = false, nullable = false)
    private Long id;

    @Column(name = "NAME", length = 1500)
    private String name;

    @Column(name = "ID_SPOTIFY", nullable = false, length = 1000)
    private String idSpotify;

    @Column(name = "ARTIST_NAME", nullable = false, length = 1500)
    private String artistName;

    @Column(name = "IMAGE_URL", nullable = false, length = 1500)
    private String imageUrl;

    @Column(name = "VALUE", nullable = false)
    private BigDecimal value;

    @Column(name = "USER_ID")
    private long userId;
}
