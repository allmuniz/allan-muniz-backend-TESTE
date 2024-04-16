package br.com.sysmap.bootcamp.domain.entities.wallet;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

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

    @OneToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private UserEntity userEntity;
    @Column(name = "user_id")
    private long userId;

    private Integer balance;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
