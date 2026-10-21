package br.edu.fiap.cartoes.exception;
/** Use em transições inválidas, tipo indisponível ou duplicidade de pedido. */
public class RegraNegocioException extends RuntimeException {
    public RegraNegocioException(String mensagem) { super(mensagem); }
}
