package br.edu.fiap.cartoes.exception;
/** Mesmo 404 para ID inexistente ou recurso de outro titular, sem revelar sua existência. */
public class RecursoNaoEncontradoException extends RuntimeException {

    public RecursoNaoEncontradoException(String mensagem) {
        super(mensagem);
    }

    public static RecursoNaoEncontradoException usuarioNaoEncontrado() {
        return new RecursoNaoEncontradoException(
                "Usuário não encontrado."
        );
    }

    public static RecursoNaoEncontradoException tipoCartaoNaoEncontrado() {
        return new RecursoNaoEncontradoException(
                "Tipo de cartão não encontrado."
        );
    }

    public static RecursoNaoEncontradoException solicitacaoNaoEncontrada() {
        return new RecursoNaoEncontradoException(
                "Solicitação não encontrada."
        );
    }
}