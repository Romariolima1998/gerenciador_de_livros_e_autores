package com.romario.gerenciador_livro_autor.business.exceptions;


import org.springframework.security.core.AuthenticationException;

public class UnaltorizedException extends AuthenticationException {

    public UnaltorizedException(String message) {
        super(message);
    }

    public UnaltorizedException(String message, Throwable cause) {
        super(message, cause);
    }
}
