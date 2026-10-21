package br.edu.fiap.cartoes.exception;
/** Falha explícita da casca didática: não simula sucesso, token ou persistência. */
public class EtapaNaoImplementadaException extends RuntimeException {
    public EtapaNaoImplementadaException(String etapa) { super("TODO implementar " + etapa); }
}
