package br.com.sysmap.bootcamp.domain.services;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserFoundException;
import br.com.sysmap.bootcamp.domain.entities.user.exceptions.UserNotFoundException;
import br.com.sysmap.bootcamp.domain.repositories.UserRepository;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final WalletService walletService;
    private final PasswordEncoder passwordEncoder;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity save(UserDto userDto) {

        this.userRepository.findByEmail(userDto.getEmail()).ifPresent((user) ->{
            throw new UserFoundException();
        });

        var password = passwordEncoder.encode(userDto.getPassword());

        var userEntity = UserEntity.builder()
                        .name(userDto.getName())
                        .email(userDto.getEmail())
                        .password(password)
                        .build();
        this.userRepository.save(userEntity);
        this.walletService.saveWallet(userEntity.getId());

        return userEntity;
    }

    public AuthDto auth(AuthDto authDto) {
        UserEntity userEntity = this.findByEmail(authDto.getEmail());

        if (!this.passwordEncoder.matches(authDto.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String password = userEntity.getEmail() + ":" + userEntity.getPassword();

        return AuthDto.builder().email(userEntity.getEmail()).token(
                Base64.getEncoder().withoutPadding().encodeToString(password.getBytes())
        ).id(userEntity.getId()).build();
    }

    public UserEntity update(UserDto userDto) {
        var user = getUser();

        var password = passwordEncoder.encode(userDto.getPassword());

        var userentity = userRepository.findByEmail(user.get().getEmail()).orElseThrow(UserNotFoundException::new);
        userentity.setName(userDto.getName());
        userentity.setEmail(userDto.getEmail());
        userentity.setPassword(password);

        return userRepository.save(userentity);
    }

    public UserEntity findById(Long id) {
        return this.userRepository.findById(id).orElseThrow(UserNotFoundException::new);
    }

    public List<UserEntity> findAll() {
        return this.userRepository.findAll();
    }



    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<UserEntity> usersOptional = this.userRepository.findByEmail(username);

        return usersOptional.map(users -> new User(users.getEmail(), users.getPassword(), new ArrayList<>()))
                .orElseThrow(() -> new UsernameNotFoundException("User not found" + username));
    }

    public UserEntity findByEmail(String email) {
        return this.userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
    }

    private Optional<UserEntity> getUser() {
        String username = SecurityContextHolder.getContext().getAuthentication()
                .getPrincipal().toString();
        return userRepository.findByEmail(username);
    }
}
