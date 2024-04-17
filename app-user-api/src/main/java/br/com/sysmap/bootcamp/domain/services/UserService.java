package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserFoundException;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.dto.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class UserService {

    private final UserRepository userRepository;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity save(UserDto userDto) {

        this.userRepository.findByEmail(userDto.getEmail()).ifPresent((user) ->{
            throw new UserFoundException();
        });
        var userEntity = UserEntity.builder()
                        .name(userDto.getName())
                        .email(userDto.getEmail())
                        .password(userDto.getPassword())
                        .build();
        return this.userRepository.save(userEntity);
    }

    public UserEntity update(long id, UserDto userDto) {

       var user = userRepository.findById(id).orElseThrow(UserNotFoundException::new);
       user.setName(userDto.getName());
       user.setEmail(userDto.getEmail());
       user.setPassword(userDto.getPassword());

       return userRepository.save(user);
    }

    public UserEntity findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }
}
