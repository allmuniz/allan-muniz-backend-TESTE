package br.com.sysmap.bootcamp.web;

import br.com.sysmap.bootcamp.domain.services.WalletService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@RestController
@RequestMapping("/wallet")
@Tag(name = "Wallet", description = "Information of wallet")
public class WalletController {

    private final WalletService walletService;

    @PostMapping("/credit/{value}")
    @Operation(summary = "Credit Operation",
            description = "This function is responsible for carrying out the credit operation in your wallet.")
    public ResponseEntity<Object> creditWallet(@PathVariable BigDecimal value){
        try {
            var result = this.walletService.credit(value);
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    @Operation(summary = "Search wallet",
            description = "This function is responsible for looking for your wallet.")
    public ResponseEntity<Object> searchWallet(){
        try {
            var result = this.walletService.myWallet();
            return ResponseEntity.ok().body(result);
        }catch (Exception e){
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
