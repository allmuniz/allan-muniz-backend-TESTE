package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.entities.wallet.exceptions.WalletNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public void saveWallet(long idUser) {
        this.userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

        var wallet = WalletEntity.builder()
                .userId(idUser)
                .balance(new BigDecimal(1999))
                .build();
        walletRepository.save(wallet);
    }

    public WalletEntity credit(long idWallet , Integer value){
        var wallet = walletRepository.findById(idWallet).orElseThrow(WalletNotFoundException::new);
        wallet.setBalance(wallet.getBalance().add(new BigDecimal(value)));
        return walletRepository.save(wallet);
    }

    public void debit(WalletDto walletDto){
        var user = userRepository.findByEmail(walletDto.getEmail()).orElseThrow(UserNotFoundException::new);
        var wallet = walletRepository.findByUserId(user.getId()).orElseThrow(WalletNotFoundException::new);
        if(walletDto.getValue().compareTo(wallet.getBalance()) <= 0){
          wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));
          walletRepository.save(wallet);
        }
        log.info(wallet.toString());
    }

    public WalletEntity myWallet(long idUser){
        return walletRepository.findByUserId(idUser).orElseThrow(WalletNotFoundException::new);
    }
}
