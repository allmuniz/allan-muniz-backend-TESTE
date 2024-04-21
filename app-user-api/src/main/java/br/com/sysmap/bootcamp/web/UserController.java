package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.entities.user.UserEntity;
import br.com.sysmap.bootcamp.domain.services.UserService;
import br.com.sysmap.bootcamp.dto.AuthDto;
import br.com.sysmap.bootcamp.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/users")
@Tag(name = "User", description = "Users API")
public class UserController {

    private final UserService userService;

    @PostMapping("/create")
    @Operation(summary = "Save user",
            description = "This function is responsible for register a user.")
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto){      //PRONTO

        try {
            var result = this.userService.save(userDto);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/auth")
    @Operation(summary = "Auth User",
            description = "This function is responsible for authenticate user access.")
    public ResponseEntity<AuthDto> auth(@RequestBody AuthDto user) {
        return ResponseEntity.ok(this.userService.auth(user));
    }

    @PutMapping("/update")
    @Operation(summary = "Update User",
            description = "This function is responsible for update user.")
    public ResponseEntity<Object> updateUser(@RequestBody UserDto userDto){
        try {
            var result = this.userService.update(userDto);
            return ResponseEntity.ok().body(result);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID",
            description = "This function is responsible for search user by id.")
    public ResponseEntity<Object> searchUser(@PathVariable() long id){
        try {
            var result = this.userService.findById(id);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "List users",
            description = "This function is responsible for search all user.")
    public ResponseEntity<Object> searchAllUsers(){
        try {
            var result = this.userService.findAll();
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
