package br.com.sysmap.bootcamp.domain.entities.user.exceptions;

public class UserNotFoundException extends RuntimeException {

    public UserNotFoundException() {super("User not found");}
}
