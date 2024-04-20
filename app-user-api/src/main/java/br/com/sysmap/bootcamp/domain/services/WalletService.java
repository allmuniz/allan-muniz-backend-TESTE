package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.entities.wallet.exceptions.WalletNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import br.com.sysmap.bootcamp.dto.WalletDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;


@Service
public class WalletService {

    private static final Logger log = LoggerFactory.getLogger(WalletService.class);
    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletService(WalletRepository walletRepository, UserRepository userRepository) {
        this.walletRepository = walletRepository;
        this.userRepository = userRepository;
    }

    public void saveWallet(long userId) {
        userRepository.findById(userId).orElseThrow(UserNotFoundException::new);

        var wallet = WalletEntity.builder()
                .userId(userId)
                .balance(new BigDecimal(1999))
                .build();
        walletRepository.save(wallet);
    }

    public WalletEntity credit(BigDecimal value){
        var user = getUser();
        var wallet = walletRepository.findById(user.get().getId()).orElseThrow(WalletNotFoundException::new);

        wallet.setBalance(wallet.getBalance().add(new BigDecimal(String.valueOf(value))));
        return walletRepository.save(wallet);
    }

    public void debit(WalletDto walletDto){

        var user = userRepository.findByEmail(walletDto.getEmail()).orElseThrow(UserNotFoundException::new);
        var wallet = walletRepository.findByUserId(user.getId()).orElseThrow(WalletNotFoundException::new);

        if(walletDto.getValue().compareTo(wallet.getBalance()) <= 0){
          wallet.setBalance(wallet.getBalance().subtract(walletDto.getValue()));

            var dayOfWeek = wallet.getLastUpdate().getDayOfWeek();
            int pointsToAdd = switch (dayOfWeek) {
                case MONDAY -> 7;
                case TUESDAY -> 6;
                case WEDNESDAY -> 2;
                case THURSDAY -> 10;
                case FRIDAY -> 15;
                case SATURDAY -> 20;
                case SUNDAY -> 25;
            };
            wallet.setPoints(wallet.getPoints() + pointsToAdd);
            walletRepository.save(wallet);
        }
        log.info(wallet.toString());
    }

    public WalletEntity myWallet(){
        var user = getUser();
        return walletRepository.findByUserId(user.get().getId()).orElseThrow(WalletNotFoundException::new);
    }

    private Optional<UserEntity> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return userRepository.findByEmail(username);
    }
}
