package br.edu.fiap.cartoes.exception;

public class EntradaInvalidaException extends RuntimeException {

    public EntradaInvalidaException(String mensagem) {
        super(mensagem);
    }

    public static EntradaInvalidaException nomeUsuarioInvalido() {
        return new EntradaInvalidaException(
                "Nome não deve ser vazio e deve possuir no máximo 100 caracteres."
        );
    }

    public static EntradaInvalidaException senhaExcedeLimiteBCrypt() {
        return new EntradaInvalidaException(
                "A senha não pode ultrapassar 72 bytes em UTF-8."
        );
    }

    public static EntradaInvalidaException senhaHashInvalida() {
        return new EntradaInvalidaException(
                "O hash da senha deve ser informado."
        );
    }
}