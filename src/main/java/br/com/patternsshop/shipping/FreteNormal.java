package br.com.patternsshop.shipping;

import br.com.patternsshop.model.Pedido;
import java.math.BigDecimal;

public final class FreteNormal implements CalculadoraFrete {
    @Override
    public BigDecimal calcular(Pedido pedido) {
        return pedido.subtotal().compareTo(new BigDecimal("200.00")) >= 0
                ? new BigDecimal("0.00") : new BigDecimal("15.00");
    }
}
