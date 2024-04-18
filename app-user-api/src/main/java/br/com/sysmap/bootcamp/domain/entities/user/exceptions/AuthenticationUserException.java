package br.com.sysmap.bootcamp.domain.entities.user.exceptions;

public class AuthenticationUserException extends RuntimeException {

    public AuthenticationUserException() {super("Email/password incorrect");}
}
