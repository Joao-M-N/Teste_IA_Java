package br.com.patternsshop.validation;

import br.com.patternsshop.model.Pedido;

/** Chain of Responsibility: cada elo valida e delega ao próximo. */
public abstract class ValidacaoPedido {
    private final ValidacaoPedido proxima;

    protected ValidacaoPedido(ValidacaoPedido proxima) {
        this.proxima = proxima;
    }

    public final void validar(Pedido pedido) {
        verificar(pedido);
        if (proxima != null) {
            proxima.validar(pedido);
        }
    }

    protected abstract void verificar(Pedido pedido);
}
