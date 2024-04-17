package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.entities.wallet.WalletEntity;
import br.com.sysmap.bootcamp.domain.entities.wallet.exceptions.InsufficientFundsException;
import br.com.sysmap.bootcamp.domain.entities.wallet.exceptions.WalletNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.domain.repositories.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletEntity saveWallet(long idUser) {

        this.userRepository.findById(idUser).orElseThrow(UserNotFoundException::new);

        var wallet = WalletEntity.builder()
                .userId(idUser)
                .balance(0)
                .build();
        return walletRepository.save(wallet);
    }

    public WalletEntity credit(long idWallet , Integer value){

        var wallet = walletRepository.findById(idWallet).orElseThrow(WalletNotFoundException::new);
        wallet.setBalance(wallet.getBalance() + value);
        return walletRepository.save(wallet);
    }

    public WalletEntity debit(long idWallet ,Integer value){

        var wallet = walletRepository.findById(idWallet).orElseThrow(WalletNotFoundException::new);

        if (value <= wallet.getBalance()){
            wallet.setBalance(wallet.getBalance() - value);
        }else {
            throw new InsufficientFundsException();
        }
        return walletRepository.save(wallet);
    }

    public WalletEntity myWallet(long idUser){
        return walletRepository.findByUserId(idUser).orElseThrow(WalletNotFoundException::new);
    }
}