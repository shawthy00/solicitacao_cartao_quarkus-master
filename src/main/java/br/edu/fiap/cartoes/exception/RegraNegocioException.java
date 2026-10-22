package br.edu.fiap.cartoes.exception;
/** Use em transições inválidas, tipo indisponível ou duplicidade de pedido. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) { super(mensagem); }

    public static RegraNegocioException cancelaApenasPendente() {
        return new RegraNegocioException("Somente solicitação pendente pode ser cancelada.");
    }

    public static RegraNegocioException emiteApenasPendente() {
        return new RegraNegocioException("Somente solicitação pendente migra para emitida.");
    }

    public static RegraNegocioException validaNomeUsuario() {
        return new RegraNegocioException("Nome não deve ser vazio e deve ter menos que 100 caracteres.");
    }

    public static RegraNegocioException validaSenhaHash() {
        return new RegraNegocioException("O hash da senha deve ser informado.");
    }

    public static RegraNegocioException disponibilidadeNaoAlterada() {
        return new RegraNegocioException("A disponibilidade do cartão não foi alterada.");
    }

    public static RegraNegocioException emailJaCadastrado() {
        return new RegraNegocioException("E-mail já cadastrado.");
    }

    public static RegraNegocioException senhaExcedeLimiteBCrypt() {
        return new RegraNegocioException("A senha não pode ultrapassar 72 bytes em UTF-8.");
    }

    public static RegraNegocioException credenciaisInvalidas() {
        return new RegraNegocioException("E-mail ou senha inválidos.");
    }

}

