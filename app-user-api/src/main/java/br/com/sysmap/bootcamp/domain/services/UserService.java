package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity save(UserEntity userEntity) {

        this.userRepository.findByEmail(userEntity.getEmail()).ifPresent((user) ->{
            throw new UserFoundException();
        });

        log.info("Saving user: {}", userEntity);
        return this.userRepository.save(userEntity);
    }
}
