package br.com.sysmap.bootcamp.domain.entities.exceptions;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(){super("Album not found");}
}
