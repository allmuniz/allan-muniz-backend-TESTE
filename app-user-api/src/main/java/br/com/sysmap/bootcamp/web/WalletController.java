package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/user/wallet")
@Tag(name = "Wallet", description = "Information of wallet")
public class WalletController {

    private final WalletService walletService;

    @PutMapping("/{walletId}/credit")
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

    @GetMapping("/{userId}")
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
