package br.com.patternsshop.shipping;

import br.com.patternsshop.model.Pedido;
import java.math.BigDecimal;

public final class FreteExpresso implements CalculadoraFrete {
    @Override
    public BigDecimal calcular(Pedido pedido) {
        return new BigDecimal("25.00").add(new BigDecimal("2.00")
                .multiply(BigDecimal.valueOf(pedido.quantidadeTotal())));
    }
}
