package br.com.patternsshop.validation;

import br.com.patternsshop.model.Pedido;

public final class ItensObrigatorios extends ValidacaoPedido {
    public ItensObrigatorios(ValidacaoPedido proxima) {
        super(proxima);
    }

    @Override
    protected void verificar(Pedido pedido) {
        if (pedido.itens().isEmpty()) {
            throw new IllegalArgumentException("O pedido deve conter pelo menos um item");
        }
    }
}
