package br.com.sysmap.bootcamp.domain.listeners;

import br.com.sysmap.bootcamp.domain.services.WalletService;
import br.com.sysmap.bootcamp.dto.WalletDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Slf4j
@RabbitListener(queues = "WalletQueue")
@Component
public class WalletListener {

    private final WalletService walletService;

    @RabbitHandler
    public void receive(WalletDto walletDto) {
        walletService.debit(walletDto);
        log.info("Received walletDto: {}", walletDto);
    }
}
