package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.services.UserService;
import br.com.sysmap.bootcamp.dto.UserDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
@Tag(name = "User", description = "Information of user")
public class UserController {

    private final UserService userService;

    @PostMapping("/")
    @Operation(summary = "User Registration",
            description = "This function is responsible for register a user.")
    public ResponseEntity<Object> createUser(@RequestBody UserDto userDto){

        try {
            var result = this.userService.save(userDto);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update User",
            description = "This function is responsible for update user.")
    public ResponseEntity<Object> updateUser(@PathVariable long id ,@RequestBody UserDto userDto){
        try {
            var result = this.userService.update(id, userDto);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Search user",
            description = "This function is responsible for search user by id.")
    public ResponseEntity<Object> searchUser(@PathVariable() long id){
        try {
            var result = this.userService.findById(id);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(summary = "Search All User",
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
