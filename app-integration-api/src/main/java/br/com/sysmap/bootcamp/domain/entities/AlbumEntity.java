package br.com.sysmap.bootcamp.domain.entities;

import io.swagger.v3.core.util.Json;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_ALBUMS")
public class AlbumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    private long id;

    private String albumType;
    private String artists;
    private String externalUrl;
    private String albumId;
    private String images;
    private String releaseDate;

    private BigDecimal value;
    private long userId;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
