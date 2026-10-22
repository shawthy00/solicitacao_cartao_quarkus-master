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

    public static RegraNegocioException emailJaCadastrado() {
        return new RegraNegocioException("E-mail já cadastrado.");
    }

    public static RegraNegocioException tipoCartaoIndisponivel() {
        return new RegraNegocioException("Tipo de cartão indisponível para solicitação.");
    }

    public static RegraNegocioException solicitacaoPendenteDuplicada() {
        return new RegraNegocioException("Já existe uma solicitação pendente para este tipo de cartão.");
    }

    public static RegraNegocioException cartaoJaEmitido() {
        return new RegraNegocioException("Já existe um cartão emitido para esta solicitação.");
    }
}

