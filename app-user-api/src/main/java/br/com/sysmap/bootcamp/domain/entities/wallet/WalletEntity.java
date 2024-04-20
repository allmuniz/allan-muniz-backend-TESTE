package br.com.sysmap.bootcamp.domain.entities.wallet;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder(toBuilder = true)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "TB_WALLETS")
public class WalletEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private BigDecimal balance;

    private int points;

    @UpdateTimestamp
    @Column( name = "last_update")
    private LocalDateTime lastUpdate;

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity userEntity;
    @Column(name = "user_id")
    private long userId;
}
