package br.com.sysmap.bootcamp.domain.entities.wallet.exceptions;

public class InsufficientFundsException extends RuntimeException {

    public InsufficientFundsException() {super("Insufficient funds");}
}
