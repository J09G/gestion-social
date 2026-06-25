package com.bodega.gestion_social.exception;

public class RecursoDuplicadoException extends RuntimeException {

    private final String recurso;
    private final String campo;
    private final String valor;

    public RecursoDuplicadoException(String recurso, String campo, String valor) {
        super(String.format("%s ya existe con %s: '%s'", recurso, campo, valor));
        this.recurso = recurso;
        this.campo = campo;
        this.valor = valor;
    }

    public String getRecurso() {
        return recurso;
    }

    public String getCampo() {
        return campo;
    }

    public String getValor() {
        return valor;
    }
}