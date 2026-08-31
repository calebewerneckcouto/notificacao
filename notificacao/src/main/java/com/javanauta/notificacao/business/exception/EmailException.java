package com.javanauta.notificacao.business.exception;

public class EmailException extends RuntimeException{

    public EmailException(String mensagem){
        super(mensagem);
    }

    EmailException(String mensagem,Throwable throwable){
        super(mensagem,throwable);
    }
}
