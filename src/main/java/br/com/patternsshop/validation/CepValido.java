package br.com.patternsshop.validation;

import br.com.patternsshop.model.Pedido;

public final class CepValido extends ValidacaoPedido {
    public CepValido(ValidacaoPedido proxima) {
        super(proxima);
    }

    @Override
    protected void verificar(Pedido pedido) {
        if (!pedido.cep().matches("[0-9]{5}-?[0-9]{3}")) {
            throw new IllegalArgumentException("Informe o CEP com 8 dígitos, com ou sem hífen");
        }
    }
}
