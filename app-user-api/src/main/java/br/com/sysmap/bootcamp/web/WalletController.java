package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.services.WalletService;
import br.com.sysmap.bootcamp.dto.WalletRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user/wallet")
@Tag(name = "Wallet", description = "Information of wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/")
//    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Create the user's wallet",
            description = "This function is responsible for create the wallet.")
    public ResponseEntity<Object> createWallet(@RequestBody WalletRequestDto walletRequestDto){
        try {

            var result = this.walletService.saveWallet(walletRequestDto.getUserId());
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{walletId}/credit")
//    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Credit Operation",
            description = "This function is responsible for carrying out the credit operation in your wallet.")
    public ResponseEntity<Object> creditWallet(@PathVariable long walletId, @RequestParam Integer value){
        try {
            var result = this.walletService.credit(walletId, value);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{walletId}/debit")
//    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Debit Operation",
            description = "This function is responsible for carrying out the debit operation in your wallet.")
    public ResponseEntity<Object> debitWallet(@PathVariable long walletId, @RequestParam Integer value){
        try {
            var result = this.walletService.debit(walletId, value);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{userId}")
//    @PreAuthorize("hasRole('ROLE_USER')")
    @Operation(summary = "Search wallet",
            description = "This function is responsible for looking for your wallet.")
    public ResponseEntity<Object> searchWallet(@PathVariable long userId){
        try {
            var result = this.walletService.myWallet(userId);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
