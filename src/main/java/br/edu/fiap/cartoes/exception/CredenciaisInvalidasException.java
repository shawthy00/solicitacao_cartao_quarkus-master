package br.edu.fiap.cartoes.exception;
/** Login sempre usa mensagem genérica, sem informar se o e-mail existe. */
public class CredenciaisInvalidasException extends RuntimeException {
    public CredenciaisInvalidasException(String mensagem) {
        super(mensagem);
    }

    public static CredenciaisInvalidasException credenciaisInvalidas() {
        return new CredenciaisInvalidasException(
                "E-mail ou senha inválidos."
        );
    }
}