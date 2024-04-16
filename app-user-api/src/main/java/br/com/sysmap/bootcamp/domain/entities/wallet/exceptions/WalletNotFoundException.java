package br.com.sysmap.bootcamp.domain.entities.wallet.exceptions;

public class WalletNotFoundException extends RuntimeException{

    public WalletNotFoundException() {super("User not found");}
}
