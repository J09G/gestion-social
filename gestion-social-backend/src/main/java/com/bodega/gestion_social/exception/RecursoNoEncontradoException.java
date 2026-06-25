package com.bodega.gestion_social.exception;

public class RecursoNoEncontradoException extends RuntimeException {

    private final String recurso;
    private final String campo;
    private final String valor;

    public RecursoNoEncontradoException(String recurso, String campo, String valor) {
        super(String.format("%s no encontrado con %s: '%s'", recurso, campo, valor));
        this.recurso = recurso;
        this.campo = campo;
        this.valor = valor;
    }

    public RecursoNoEncontradoException(String recurso) {
        super(recurso + " no encontrado");
        this.recurso = recurso;
        this.campo = null;
        this.valor = null;
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