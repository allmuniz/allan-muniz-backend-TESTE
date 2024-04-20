package br.com.sysmap.bootcamp.domain.entities.album.exceptions;

public class AlbumNotFoundException extends RuntimeException{
    public AlbumNotFoundException(){super("Album not found");}
}
